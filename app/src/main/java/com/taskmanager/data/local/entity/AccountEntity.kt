package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val initialBalance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "RUB"
)
