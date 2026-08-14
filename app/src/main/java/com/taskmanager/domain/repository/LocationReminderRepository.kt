package com.taskmanager.domain.repository

import com.taskmanager.domain.model.LocationReminder
import kotlinx.coroutines.flow.Flow

interface LocationReminderRepository {
    suspend fun create(reminder: LocationReminder): Long
    suspend fun getById(id: Long): LocationReminder?
    suspend fun getActive(): List<LocationReminder>
    suspend fun setActive(id: Long, active: Boolean)
    suspend fun delete(id: Long)
    fun observeAll(): Flow<List<LocationReminder>>
    fun observeByTask(taskId: Long): Flow<List<LocationReminder>>
}
