package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskmanager.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: UserStatsEntity)

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun observe(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun get(): UserStatsEntity?

    @Query("SELECT * FROM achievements WHERE required_task_count <= :taskCount")
    suspend fun getAchievementsForTaskCount(taskCount: Int): List<com.taskmanager.data.local.entity.AchievementEntity>
}
