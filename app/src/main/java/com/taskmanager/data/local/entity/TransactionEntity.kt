package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "transactions",
    indices = [
        Index("type"),
        Index("categoryId"),
        Index("accountId"),
        Index("date"),
        Index("currency")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: BigDecimal,
    val type: String,
    val currency: String = "RUB",
    val categoryId: Long? = null,
    val accountId: Long? = null,
    val date: Long,
    val note: String? = null,
    val toAccountId: Long? = null,
    val destinationAmount: BigDecimal? = null,
    val destinationCurrency: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
