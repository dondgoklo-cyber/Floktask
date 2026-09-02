package com.taskmanager.domain.model

import java.math.BigDecimal

data class Budget(
    val id: Long? = null,
    val categoryId: Long,
    val amount: BigDecimal,
    val currency: String = "RUB"
)
