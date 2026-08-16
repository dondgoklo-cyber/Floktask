package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [
        Index("folderId"),
        Index("projectId"),
        Index("pinned"),
        Index("archived"),
        Index("updatedAt")
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val contentMarkdown: String = "",
    val folderId: Long? = null,
    val tags: String? = null,
    val pinned: Boolean = false,
    val archived: Boolean = false,
    val projectId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
