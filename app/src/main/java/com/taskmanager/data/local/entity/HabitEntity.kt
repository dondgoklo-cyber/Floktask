package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits",
    indices = [Index("isArchived")]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null,
    val frequency: String = "DAILY",
    /** Дни недели для WEEKLY-частоты (CSV: 1=Пн ... 7=Вс). */
    val daysOfWeek: String = "",
    val targetCount: Int = 1,
    val reminderTime: Long? = null,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
