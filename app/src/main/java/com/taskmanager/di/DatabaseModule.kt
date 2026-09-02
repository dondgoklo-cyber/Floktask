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
import com.taskmanager.data.local.dao.SubprojectDao
import com.taskmanager.data.local.dao.SubtaskDao
import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.dao.TaskTagDao
import com.taskmanager.data.local.dao.TransactionDao
import com.taskmanager.data.local.dao.UserStatsDao
import com.taskmanager.data.local.database.AppDatabase
import com.taskmanager.data.local.database.Migrations
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
    )
        .addMigrations(*Migrations.ALL)
        // Только при откате версии разрешаем пересоздание; upgrade всегда идёт через миграции.
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideProjectDao(db: AppDatabase): ProjectDao = db.projectDao()

    @Provides
    fun provideSubprojectDao(db: AppDatabase): SubprojectDao = db.subprojectDao()

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
