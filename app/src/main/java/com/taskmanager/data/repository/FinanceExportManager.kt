package com.taskmanager.data.repository

import com.taskmanager.domain.model.Account
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.model.TransactionType
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экспорт финансовых данных в CSV и JSON.
 * CSV пригоден для Excel/Google Sheets.
 * JSON пригоден для backup/import.
 */
class FinanceExportManager {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    /**
     * Экспортирует транзакции в CSV.
     * Колонки: Date,Type,Amount,Currency,Category,Account,Description,Tags
     */
    fun exportToCsv(
        transactions: List<Transaction>,
        categories: List<Category>,
        accounts: List<Account>,
        writer: Writer
    ) {
        writer.write("Date,Type,Amount,Currency,Category,Account,Description\n")

        transactions.forEach { tx ->
            val catName = categories.find { it.id == tx.categoryId }?.name ?: ""
            val accName = accounts.find { it.id == tx.accountId }?.name ?: ""
            val dateStr = dateFormat.format(Date(tx.date.toEpochMilli()))
            val typeStr = when (tx.type) {
                TransactionType.INCOME -> "Доход"
                TransactionType.EXPENSE -> "Расход"
                TransactionType.TRANSFER -> "Перевод"
            }
            val note = tx.note?.replace(",", ";")?.replace("\n", " ") ?: ""

            writer.write("$dateStr,$typeStr,${tx.amount},${tx.currency},$catName,$accName,$note\n")
        }
        writer.flush()
    }

    /**
     * Экспортирует все финансовые данные в JSON.
     * Включает: accounts, transactions, categories.
     */
    fun exportToJson(
        transactions: List<Transaction>,
        categories: List<Category>,
        accounts: List<Account>
    ): String {
        val root = JSONObject()

        // Accounts
        val accountsArray = JSONArray()
        accounts.forEach { acc ->
            accountsArray.put(JSONObject().apply {
                put("id", acc.id ?: 0)
                put("name", acc.name)
                put("initialBalance", acc.initialBalance)
                put("currency", acc.currency)
            })
        }
        root.put("accounts", accountsArray)

        // Categories
        val categoriesArray = JSONArray()
        categories.forEach { cat ->
            categoriesArray.put(JSONObject().apply {
                put("id", cat.id ?: 0)
                put("name", cat.name)
                put("type", cat.type.name)
                put("color", cat.color ?: JSONObject.NULL)
                put("isDefault", cat.isDefault)
            })
        }
        root.put("categories", categoriesArray)

        // Transactions
        val txArray = JSONArray()
        transactions.forEach { tx ->
            txArray.put(JSONObject().apply {
                put("id", tx.id ?: 0)
                put("amount", tx.amount)
                put("type", tx.type.name)
                put("currency", tx.currency)
                put("categoryId", tx.categoryId ?: JSONObject.NULL)
                put("accountId", tx.accountId ?: JSONObject.NULL)
                put("date", tx.date.toEpochMilli())
                put("note", tx.note ?: JSONObject.NULL)
                put("toAccountId", tx.toAccountId ?: JSONObject.NULL)
                put("destinationAmount", tx.destinationAmount ?: JSONObject.NULL)
                put("destinationCurrency", tx.destinationCurrency ?: JSONObject.NULL)
            })
        }
        root.put("transactions", txArray)

        return root.toString(2)
    }

    /**
     * Импортирует финансовые данные из JSON.
     * Возвращает список транзакций для создания.
     * Валидирует сумму (>0), дату, тип.
     */
    fun importFromJson(jsonStr: String): List<Transaction> {
        val result = mutableListOf<Transaction>()
        val root = runCatching { JSONObject(jsonStr) }.getOrNull() ?: return emptyList()
        val txArray = root.optJSONArray("transactions") ?: return emptyList()

        for (i in 0 until txArray.length()) {
            val txJson = txArray.optJSONObject(i) ?: continue
            val amount = txJson.optDouble("amount", 0.0)
            if (amount <= 0) continue
            val typeStr = txJson.optString("type", "EXPENSE")
            val type = runCatching { TransactionType.valueOf(typeStr) }.getOrDefault(TransactionType.EXPENSE)
            val dateMillis = txJson.optLong("date", System.currentTimeMillis())
            val currency = txJson.optString("currency", "RUB")

            result.add(Transaction(
                amount = amount,
                type = type,
                currency = currency,
                categoryId = if (txJson.isNull("categoryId")) null else txJson.optLong("categoryId", 0),
                accountId = if (txJson.isNull("accountId")) null else txJson.optLong("accountId", 0),
                date = java.time.Instant.ofEpochMilli(dateMillis),
                note = if (txJson.isNull("note")) null else txJson.optString("note", null)
            ))
        }
        return result
    }

    /**
     * Генерирует имя файла для экспорта.
     */
    fun generateFileName(prefix: String, extension: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "${prefix}_$timestamp.$extension"
    }
}
