package com.taskmanager.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taskmanager.data.local.dao.AccountDao
import com.taskmanager.data.local.dao.CategoryDao
import com.taskmanager.data.local.dao.HabitDao
import com.taskmanager.data.local.dao.HabitLogDao
import com.taskmanager.data.local.dao.PomodoroSessionDao
import com.taskmanager.data.local.dao.ProjectDao
import com.taskmanager.data.local.dao.SubtaskDao
import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.dao.TransactionDao
import com.taskmanager.data.local.dao.UserStatsDao
import com.taskmanager.data.local.entity.AccountEntity
import com.taskmanager.data.local.entity.CategoryEntity
import com.taskmanager.data.local.entity.HabitEntity
import com.taskmanager.data.local.entity.HabitLogEntity
import com.taskmanager.data.local.entity.PomodoroSessionEntity
import com.taskmanager.data.local.entity.ProjectEntity
import com.taskmanager.data.local.entity.SubtaskEntity
import com.taskmanager.data.local.entity.TagEntity
import com.taskmanager.data.local.entity.TaskEntity
import com.taskmanager.data.local.entity.TransactionEntity
import com.taskmanager.data.local.entity.UserStatsEntity

@Database(
    entities = [
        TaskEntity::class,
        ProjectEntity::class,
        TagEntity::class,
        UserStatsEntity::class,
        SubtaskEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        PomodoroSessionEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun tagDao(): TagDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun pomodoroSessionDao(): PomodoroSessionDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao

    companion object {
        const val DATABASE_NAME = "taskmanager.db"
    }
}
