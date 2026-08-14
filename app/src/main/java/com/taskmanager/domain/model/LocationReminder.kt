package com.taskmanager.domain.model

import java.time.Instant

data class LocationReminder(
    val id: Long? = null,
    val taskId: Long,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 150f,
    val isActive: Boolean = true,
    val createdAt: Instant = Instant.now()
)
