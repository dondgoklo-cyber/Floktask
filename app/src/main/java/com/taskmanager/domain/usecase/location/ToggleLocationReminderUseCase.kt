package com.taskmanager.domain.usecase.location

import com.taskmanager.domain.repository.LocationReminderRepository
import javax.inject.Inject

class ToggleLocationReminderUseCase @Inject constructor(
    private val repository: LocationReminderRepository
) {
    suspend operator fun invoke(id: Long, active: Boolean) =
        repository.setActive(id, active)
}
