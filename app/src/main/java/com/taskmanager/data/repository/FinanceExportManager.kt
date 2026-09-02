package com.taskmanager.data.repository

import com.taskmanager.domain.model.Account
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.model.TransactionType
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.math.BigDecimal
import java.io.Writer
import java.time.Instant
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
     * Экспортирует транзакции в CSV (RFC 4180).
     * Колонки: Date,Type,Amount,Currency,Category,Account,Description
     * Поля корректно экранируются: поля с запятой, кавычкой или переводом строки
     * заключаются в двойные кавычки, внутренние кавычки удваиваются.
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
            val note = csvEscape(tx.note ?: "")

            writer.write(buildString {
                append(csvEscape(dateStr)).append(',')
                append(csvEscape(typeStr)).append(',')
                append(csvEscape(tx.amount.toString())).append(',')
                append(csvEscape(tx.currency)).append(',')
                append(csvEscape(catName)).append(',')
                append(csvEscape(accName)).append(',')
                append(note)
                append('\n')
            })
        }
        writer.flush()
    }

    /** RFC 4180: заключить в кавычки при наличии запятой/кавычки/CRLF, удвоить кавычки. */
    private fun csvEscape(field: String): String {
        val needsQuoting = field.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (needsQuoting) "\"" + field.replace("\"", "\"\"") + "\"" else field
    }

    /**
     * Парсит CSV, созданный [exportToCsv], обратно в список транзакций (round-trip).
     * Восстанавливает Date/Type/Amount/Currency/Category/Account/Description.
     * categoryId/accountId сопоставляются по имени с переданными справочниками.
     */
    fun importFromCsv(
        csv: String,
        categories: List<Category>,
        accounts: List<Account>
    ): List<Transaction> {
        val rows = parseCsv(csv)
        if (rows.isEmpty()) return emptyList()
        val header = rows.first()
        val data = rows.drop(1)
        if (header != listOf("Date", "Type", "Amount", "Currency", "Category", "Account", "Description")) {
            return emptyList()
        }
        val result = mutableListOf<Transaction>()
        for (row in data) {
            if (row.size < 7) continue
            val date = runCatching { dateFormat.parse(row[0]) }.getOrNull() ?: continue
            val type = when (row[1]) {
                "Доход" -> TransactionType.INCOME
                "Расход" -> TransactionType.EXPENSE
                "Перевод" -> TransactionType.TRANSFER
                else -> runCatching { TransactionType.valueOf(row[1]) }.getOrDefault(TransactionType.EXPENSE)
            }
            val amount = try { BigDecimal(row[2]) } catch (e: NumberFormatException) { continue }
            val currency = row[3]
            val categoryId = categories.firstOrNull { it.name == row[4] }?.id
            val accountId = accounts.firstOrNull { it.name == row[5] }?.id
            val note = row[6].ifBlank { null }
            result.add(
                Transaction(
                    amount = amount,
                    type = type,
                    currency = currency,
                    categoryId = categoryId,
                    accountId = accountId,
                    date = Instant.ofEpochMilli(date.time),
                    note = note
                )
            )
        }
        return result
    }

    /** Минимальный RFC 4180 CSV-парсер: поддерживает кавычки, удвоенные кавычки, CRLF. */
    private fun parseCsv(csv: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val current = StringBuilder()
        val fields = mutableListOf<String>()
        var inQuotes = false
        var i = 0
        var rowStarted = false
        while (i < csv.length) {
            val c = csv[i]
            rowStarted = true
            when {
                inQuotes -> {
                    if (c == '"') {
                        if (i + 1 < csv.length && csv[i + 1] == '"') {
                            current.append('"'); i += 2; continue
                        }
                        inQuotes = false; i++
                    } else {
                        current.append(c); i++
                    }
                }
                c == '"' -> { inQuotes = true; i++ }
                c == ',' -> { fields.add(current.toString()); current.clear(); i++ }
                c == '\r' -> { if (i + 1 < csv.length && csv[i + 1] == '\n') i++ else i++; fields.add(current.toString()); current.clear(); if (rowStarted) rows.add(fields.toList()); fields.clear(); rowStarted = false }
                c == '\n' -> { fields.add(current.toString()); current.clear(); if (rowStarted) rows.add(fields.toList()); fields.clear(); rowStarted = false; i++ }
                else -> { current.append(c); i++ }
            }
        }
        if (rowStarted) {
            fields.add(current.toString())
            rows.add(fields.toList())
        }
        return rows
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
            val amount = if (!txJson.has("amount") || txJson.isNull("amount")) BigDecimal.ZERO else try { BigDecimal(txJson.getString("amount")) } catch (e: Exception) { BigDecimal.ZERO }
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
                note = if (txJson.isNull("note")) null else txJson.optString("note", null),
                toAccountId = if (txJson.isNull("toAccountId")) null else txJson.optLong("toAccountId", 0),
                destinationAmount = if (!txJson.has("destinationAmount") || txJson.isNull("destinationAmount")) null else try { BigDecimal(txJson.getString("destinationAmount")) } catch (e: Exception) { null },
                destinationCurrency = if (txJson.isNull("destinationCurrency")) null else txJson.optString("destinationCurrency", null)
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
