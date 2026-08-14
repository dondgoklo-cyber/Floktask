package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.LocationReminderDao
import com.taskmanager.domain.model.LocationReminder
import com.taskmanager.domain.repository.LocationReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationReminderRepositoryImpl @Inject constructor(
    private val dao: LocationReminderDao
) : LocationReminderRepository {

    override suspend fun create(reminder: LocationReminder): Long =
        dao.insert(reminder.toEntity())

    override suspend fun getById(id: Long): LocationReminder? =
        dao.getById(id)?.toDomain()

    override suspend fun getActive(): List<LocationReminder> =
        dao.getActive().map { it.toDomain() }

    override suspend fun setActive(id: Long, active: Boolean) {
        dao.setActive(id, active)
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    override fun observeAll(): Flow<List<LocationReminder>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByTask(taskId: Long): Flow<List<LocationReminder>> =
        dao.observeByTask(taskId).map { list -> list.map { it.toDomain() } }
}
