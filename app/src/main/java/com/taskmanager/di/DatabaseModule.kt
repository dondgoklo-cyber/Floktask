package com.taskmanager.di

import android.content.Context
import androidx.room.Room
import com.taskmanager.data.local.dao.AccountDao
import com.taskmanager.data.local.dao.BudgetDao
import com.taskmanager.data.local.dao.GoalDao
import com.taskmanager.data.local.dao.CategoryDao
import com.taskmanager.data.local.dao.HabitDao
import com.taskmanager.data.local.dao.NoteDao
import com.taskmanager.data.local.dao.NoteFolderDao
import com.taskmanager.data.local.dao.HabitLogDao
import com.taskmanager.data.local.dao.PomodoroSessionDao
import com.taskmanager.data.local.dao.ProjectDao
import com.taskmanager.data.local.dao.SubtaskDao
import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.dao.TaskTagDao
import com.taskmanager.data.local.dao.TransactionDao
import com.taskmanager.data.local.dao.UserStatsDao
import com.taskmanager.data.local.database.AppDatabase
import com.taskmanager.data.local.database.migration.MIGRATION_10_11
import com.taskmanager.data.local.database.migration.MIGRATION_11_12
import com.taskmanager.data.local.database.migration.MIGRATION_12_13
import com.taskmanager.data.local.database.migration.MIGRATION_13_14
import com.taskmanager.data.local.database.migration.MIGRATION_14_15
import com.taskmanager.data.local.database.migration.MIGRATION_15_16
import com.taskmanager.data.local.database.migration.MIGRATION_1_2
import com.taskmanager.data.local.database.migration.MIGRATION_2_3
import com.taskmanager.data.local.database.migration.MIGRATION_3_4
import com.taskmanager.data.local.database.migration.MIGRATION_4_5
import com.taskmanager.data.local.database.migration.MIGRATION_5_6
import com.taskmanager.data.local.database.migration.MIGRATION_6_7
import com.taskmanager.data.local.database.migration.MIGRATION_7_8
import com.taskmanager.data.local.database.migration.MIGRATION_8_9
import com.taskmanager.data.local.database.migration.MIGRATION_9_10
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).addMigrations(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7,
        MIGRATION_7_8,
        MIGRATION_8_9,
        MIGRATION_9_10,
        MIGRATION_10_11,
        MIGRATION_11_12,
        MIGRATION_12_13,
        MIGRATION_13_14,
        MIGRATION_14_15,
        MIGRATION_15_16
    ).build()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideProjectDao(db: AppDatabase): ProjectDao = db.projectDao()

    @Provides
    fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()

    @Provides
    fun provideUserStatsDao(db: AppDatabase): UserStatsDao = db.userStatsDao()

    @Provides
    fun provideSubtaskDao(db: AppDatabase): SubtaskDao = db.subtaskDao()

    @Provides
    fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()

    @Provides
    fun provideHabitLogDao(db: AppDatabase): HabitLogDao = db.habitLogDao()

    @Provides
    fun providePomodoroSessionDao(db: AppDatabase): PomodoroSessionDao = db.pomodoroSessionDao()
    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
    @Provides
    fun provideNoteFolderDao(db: AppDatabase): NoteFolderDao = db.noteFolderDao()
    @Provides
    fun provideBudgetDao(db: AppDatabase): BudgetDao = db.budgetDao()
    @Provides
    fun provideGoalDao(db: AppDatabase): GoalDao = db.goalDao()
    @Provides
    fun provideTaskTagDao(db: AppDatabase): TaskTagDao = db.taskTagDao()
}
