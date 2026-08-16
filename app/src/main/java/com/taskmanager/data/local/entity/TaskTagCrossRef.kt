package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Many-to-many join between tasks and tags.
 * CASCADE on both sides: deleting a task or tag removes the link.
 */
@Entity(
    tableName = "task_tag_cross_ref",
    primaryKeys = ["taskId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId"), Index("tagId")]
)
data class TaskTagCrossRef(
    val taskId: Long,
    val tagId: Long
)
