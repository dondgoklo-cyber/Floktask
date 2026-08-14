package com.taskmanager.domain.model

import java.time.Instant

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: Long? = null,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long? = null,
    val accountId: Long? = null,
    val date: Instant = Instant.now(),
    val note: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
