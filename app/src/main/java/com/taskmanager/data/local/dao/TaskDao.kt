package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId AND subprojectId IS NULL")
    fun getByProject(projectId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE subprojectId = :subprojectId")
    fun getBySubproject(subprojectId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId OR subprojectId IN (SELECT id FROM subprojects WHERE parentProjectId = :projectId)")
    fun getAllByProjectIncludingSubprojects(projectId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    fun getIncompleteTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE title LIKE :query OR description LIKE :query")
    fun search(query: String): Flow<List<TaskEntity>>

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE tasks SET isCompleted = :completed, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean, updatedAt: Long)

    /**
     * Задачи с запланированным временем на день [startTime в диапазоне [dayStart, dayEnd].
     * Используется для time blocking в календаре.
     */
    @Query("SELECT * FROM tasks WHERE startTime IS NOT NULL AND startTime >= :dayStart AND startTime < :dayEnd ORDER BY startTime ASC")
    fun getTimedTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<TaskEntity>>

    /**
     * Задачи на день: те, у которых deadline или startTime попадает в [dayStart, dayEnd].
     */
    @Query("SELECT * FROM tasks WHERE (deadline IS NOT NULL AND deadline >= :dayStart AND deadline < :dayEnd) OR (startTime IS NOT NULL AND startTime >= :dayStart AND startTime < :dayEnd) ORDER BY startTime IS NULL, startTime ASC, deadline ASC")
    fun getTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE eisenhowerQuadrant = :quadrant AND isCompleted = 0")
    fun getTasksByQuadrant(quadrant: String): Flow<List<TaskEntity>>

    /** Inbox: задачи без проекта и без даты (быстрый захват), не выполненные. */
    @Query("SELECT * FROM tasks WHERE projectId IS NULL AND deadline IS NULL AND startTime IS NULL AND isCompleted = 0 ORDER BY createdAt DESC")
    fun getInboxTasks(): Flow<List<TaskEntity>>

    /** Upcoming: невыполненные задачи с дедлайном/startTime в будущем, отсортированные по ближайшей дате. */
    @Query(
        """
        SELECT * FROM tasks
        WHERE isCompleted = 0
          AND (
            (deadline IS NOT NULL AND deadline >= :fromEpoch)
            OR (startTime IS NOT NULL AND startTime >= :fromEpoch)
          )
        ORDER BY
            CASE WHEN startTime IS NOT NULL THEN startTime ELSE deadline END ASC
        """
    )
    fun getUpcomingTasks(fromEpoch: Long): Flow<List<TaskEntity>>

    /** Задачи с активным напоминанием в будущем (для перерегистрации после перезагрузки). */
    @Query("SELECT * FROM tasks WHERE reminderDate IS NOT NULL AND reminderDate > :now AND isCompleted = 0")
    suspend fun getAllTasksWithReminders(now: Long): List<TaskEntity>
}
