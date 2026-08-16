package com.taskmanager.domain.model

data class Budget(
    val id: Long? = null,
    val categoryId: Long,
    val amount: Double,
    val currency: String = "RUB"
)
