package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_reminders",
    indices = [Index("taskId")]
)
data class LocationReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 150f,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
