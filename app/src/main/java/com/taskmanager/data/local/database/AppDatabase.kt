package com.taskmanager.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taskmanager.data.local.dao.ProjectDao
import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.dao.TimeBlockDao
import com.taskmanager.data.local.entity.ProjectEntity
import com.taskmanager.data.local.entity.TagEntity
import com.taskmanager.data.local.entity.TaskEntity
import com.taskmanager.data.local.entity.TimeBlockEntity

@Database(
    entities = [TaskEntity::class, ProjectEntity::class, TagEntity::class, TimeBlockEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun tagDao(): TagDao
    abstract fun timeBlockDao(): TimeBlockDao

    companion object {
        const val DATABASE_NAME = "taskmanager.db"
    }
}
