package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subprojects",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentProjectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubprojectEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentSubprojectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("parentProjectId"),
        Index("parentSubprojectId"),
        Index("color"),
        Index("isArchived"),
        Index("orderIndex")
    ]
)
data class SubprojectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val parentProjectId: Long? = null,
    val parentSubprojectId: Long? = null,
    val color: String? = null,
    val icon: String? = null,
    val deadline: Long? = null,
    val isArchived: Boolean = false,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
