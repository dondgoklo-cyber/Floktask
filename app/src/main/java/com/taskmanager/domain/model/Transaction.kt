package com.taskmanager.domain.model

import java.time.Instant

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

data class Transaction(
    val id: Long? = null,
    val amount: Double,
    val type: TransactionType,
    val currency: String = "RUB",
    val categoryId: Long? = null,
    val accountId: Long? = null,
    val date: Instant = Instant.now(),
    val note: String? = null,
    // Transfer-specific fields
    val toAccountId: Long? = null,
    val destinationAmount: Double? = null,
    val destinationCurrency: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
