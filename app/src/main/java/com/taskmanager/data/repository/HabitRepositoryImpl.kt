package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.HabitDao
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override suspend fun createHabit(habit: Habit): Long =
        habitDao.insert(habit.toEntity())

    override suspend fun getHabitById(id: Long): Habit? =
        habitDao.getById(id)?.toDomain()

    override suspend fun updateHabit(habit: Habit) {
        habitDao.update(habit.copy(updatedAt = Instant.now()).toEntity())
    }

    override suspend fun deleteHabit(id: Long) {
        habitDao.deleteById(id)
    }

    override suspend fun archiveHabit(id: Long, archived: Boolean) {
        habitDao.setArchived(id, archived, Instant.now().toEpochMilli())
    }

    override fun getActiveHabits(): Flow<List<Habit>> =
        habitDao.getActive().map { list -> list.map { it.toDomain() } }

    override fun getAllHabits(): Flow<List<Habit>> =
        habitDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getArchivedHabits(): Flow<List<Habit>> =
        habitDao.getArchived().map { list -> list.map { it.toDomain() } }
}
