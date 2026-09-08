package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task

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
    val priority: Int = Priority.MEDIUM.ordinal,
    val deadline: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val color: String? = null,
    val reminderDate: Long? = null,
    val recurrenceRule: String? = null
) {
    fun toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description.orEmpty(),
            priority = Priority.values().getOrNull(priority) ?: Priority.MEDIUM,
            deadline = deadline,
            isCompleted = isCompleted,
            projectId = projectId,
            recurrenceRule = recurrenceRule?.let { RecurrenceRule.parse(it) },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun from(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority.ordinal,
                deadline = task.deadline,
                isCompleted = task.isCompleted,
                projectId = task.projectId,
                recurrenceRule = task.recurrenceRule?.toString(),
                createdAt = task.createdAt,
                updatedAt = task.updatedAt
            )
        }
    }
}
