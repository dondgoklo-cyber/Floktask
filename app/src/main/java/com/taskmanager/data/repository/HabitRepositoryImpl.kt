package com.taskmanager.data.repository
import com.taskmanager.domain.logger.Logger

import com.taskmanager.data.local.dao.HabitDao
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val habitDao: HabitDao
) : HabitRepository {

    override suspend fun createHabit(habit: Habit): Long = try {
        habitDao.insert(habit.toEntity())
    } catch (e: Exception) {
        logger.error("HabitRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun getHabitById(id: Long): Habit? = try {
        habitDao.getById(id)?.toDomain()
    } catch (e: Exception) {
        logger.error("HabitRepositoryImpl", "Error in Habit?", e)
        throw e
    }

    override suspend fun updateHabit(habit: Habit) {
        try {
            habitDao.update(habit.copy(updatedAt = Instant.now()).toEntity())
        } catch (e: Exception) {
            logger.error("HabitRepositoryImpl", "Error in Habit", e)
            throw e
        }
    }

    override suspend fun deleteHabit(id: Long) {
        try {
            habitDao.deleteById(id)
        } catch (e: Exception) {
            logger.error("HabitRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override suspend fun archiveHabit(id: Long, archived: Boolean) {
        try {
            habitDao.setArchived(id, archived, Instant.now().toEpochMilli())
        } catch (e: Exception) {
            logger.error("HabitRepositoryImpl", "Error in Boolean", e)
            throw e
        }
    }

    override fun getActiveHabits(): Flow<List<Habit>> = try {
        habitDao.getActive().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        logger.error("HabitRepositoryImpl", "Error in Flow<List<Habit>>", e)
        throw e
    }

    override fun getAllHabits(): Flow<List<Habit>> =
        habitDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getArchivedHabits(): Flow<List<Habit>> =
        habitDao.getArchived().map { list -> list.map { it.toDomain() } }
}
