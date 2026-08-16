package com.taskmanager.domain.repository

import com.taskmanager.domain.model.TimeBlock
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TimeBlockRepository {
    suspend fun createTimeBlock(block: TimeBlock): Long
    suspend fun getTimeBlock(id: Long): TimeBlock?
    suspend fun updateTimeBlock(block: TimeBlock)
    suspend fun deleteTimeBlock(id: Long)
    fun getAllTimeBlocks(): Flow<List<TimeBlock>>
    fun getTimeBlocksByDate(date: LocalDate): Flow<List<TimeBlock>>
    fun getTimeBlocksByTask(taskId: Long): Flow<List<TimeBlock>>
    fun getTimeBlocksByProject(projectId: Long): Flow<List<TimeBlock>>
    fun getTimeBlocksBetween(startEpochMillis: Long, endEpochMillis: Long): Flow<List<TimeBlock>>
}
