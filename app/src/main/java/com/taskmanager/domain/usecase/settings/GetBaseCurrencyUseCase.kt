package com.taskmanager.domain.usecase.settings

import com.taskmanager.domain.logger.Logger
import javax.inject.Inject

/**
 * Use case for retrieving base currency from user preferences.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not direct Android dependencies.
 */
interface UserPreferences {
    val baseCurrency: String
}

class GetBaseCurrencyUseCase @Inject constructor(
    private val userPreferences: UserPreferences,
    private val logger: Logger
) {
    operator fun invoke(): String = userPreferences.baseCurrency
}
