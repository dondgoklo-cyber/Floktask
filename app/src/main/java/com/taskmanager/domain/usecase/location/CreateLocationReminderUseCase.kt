package com.taskmanager.domain.usecase.location

import com.taskmanager.domain.model.LocationReminder
import com.taskmanager.domain.repository.LocationReminderRepository
import javax.inject.Inject

class CreateLocationReminderUseCase @Inject constructor(
    private val repository: LocationReminderRepository
) {
    suspend operator fun invoke(reminder: LocationReminder): Long =
        repository.create(reminder)
}
