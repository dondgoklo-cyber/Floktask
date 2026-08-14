package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = ProjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["projectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("projectId"),
        Index("priority"),
        Index("deadline"),
        Index("isCompleted")
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val projectId: Long? = null,
    val priority: Int = 4,
    val deadline: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val color: String? = null,
    val reminderDate: Long? = null,
    val recurrenceRule: String? = null
)
