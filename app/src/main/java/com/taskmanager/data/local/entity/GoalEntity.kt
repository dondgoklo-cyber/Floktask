package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: BigDecimal,
    val savedAmount: BigDecimal = BigDecimal.ZERO,
    val currency: String = "RUB",
    val deadline: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
