package com.taskmanager.data.repository

import com.taskmanager.domain.model.Account
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.CategoryType
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.StringWriter
import java.time.Instant

class FinanceCsvRoundTripTest {

    private val manager = FinanceExportManager()

    private val categories = listOf(
        Category(id = 1, name = "Продукты", type = CategoryType.EXPENSE),
        Category(id = 2, name = "Зарплата", type = CategoryType.INCOME)
    )
    private val accounts = listOf(
        Account(id = 10, name = "Основной")
    )

    @Test
    fun `csv round trip preserves values for simple transactions`() {
        val tx = Transaction(
            id = 1, amount = 123.45, type = TransactionType.EXPENSE, currency = "RUB",
            categoryId = 1, accountId = 10, date = Instant.parse("2026-01-15T10:00:00Z"),
            note = "Молоко, хлеб"
        )
        val writer = StringWriter()
        manager.exportToCsv(listOf(tx), categories, accounts, writer)
        val csv = writer.toString()
        val parsed = manager.importFromCsv(csv, categories, accounts)
        assertEquals(1, parsed.size)
        val p = parsed.first()
        assertEquals(123.45, p.amount, 0.001)
        assertEquals(TransactionType.EXPENSE, p.type)
        assertEquals("RUB", p.currency)
        assertEquals(1L, p.categoryId)
        assertEquals(10L, p.accountId)
        assertEquals("Молоко, хлеб", p.note)
    }

    @Test
    fun `csv escaping handles quotes commas and newlines in note`() {
        val tx = Transaction(
            id = 2, amount = 50.0, type = TransactionType.EXPENSE, currency = "RUB",
            categoryId = 1, accountId = 10, date = Instant.parse("2026-01-16T12:00:00Z"),
            note = "Он сказал \"привет\", а потом\nновая строка"
        )
        val writer = StringWriter()
        manager.exportToCsv(listOf(tx), categories, accounts, writer)
        val csv = writer.toString()
        // note must be quoted and contain doubled quotes
        assertTrue("csv must quote the note field", csv.contains("\"Он сказал \"\"привет\"\", а потом\nновая строка\""))
        val parsed = manager.importFromCsv(csv, categories, accounts)
        assertEquals(1, parsed.size)
        assertEquals("Он сказал \"привет\", а потом\nновая строка", parsed.first().note)
    }

    @Test
    fun `csv header must match expected columns`() {
        val writer = StringWriter()
        manager.exportToCsv(emptyList(), categories, accounts, writer)
        val firstLine = writer.toString().lines().first()
        assertEquals("Date,Type,Amount,Currency,Category,Account,Description", firstLine)
    }

    @Test
    fun `csv import skips invalid rows`() {
        val csv = "Date,Type,Amount,Currency,Category,Account,Description\n" +
            "not-a-date,Расход,100,RUB,Продукты,Основной,x\n" +
            "2026-01-15 10:00,Расход,abc,RUB,Продукты,Основной,y\n"
        val parsed = manager.importFromCsv(csv, categories, accounts)
        assertTrue("invalid rows must be skipped", parsed.isEmpty())
    }
}
