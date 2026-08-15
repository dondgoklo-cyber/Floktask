package com.taskmanager.domain.model

import java.time.Instant

data class Goal(
    val id: Long? = null,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val currency: String = "RUB",
    val deadline: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
