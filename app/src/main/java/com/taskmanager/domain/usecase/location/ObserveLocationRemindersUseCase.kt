package com.taskmanager.domain.usecase.location

import com.taskmanager.domain.model.LocationReminder
import com.taskmanager.domain.repository.LocationReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLocationRemindersUseCase @Inject constructor(
    private val repository: LocationReminderRepository
) {
    operator fun invoke(): Flow<List<LocationReminder>> = repository.observeAll()
}
