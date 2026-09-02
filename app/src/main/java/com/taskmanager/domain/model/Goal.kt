package com.taskmanager.domain.model

import java.math.BigDecimal
import java.time.Instant

data class Goal(
    val id: Long? = null,
    val title: String,
    val targetAmount: BigDecimal,
    val savedAmount: BigDecimal = BigDecimal.ZERO,
    val currency: String = "RUB",
    val deadline: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
