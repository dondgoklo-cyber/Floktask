package com.taskmanager.data.repository

import com.taskmanager.domain.finance.ExchangeRateProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Локальный (mock) провайдер курсов валют.
 * Содержит приближённые курсы относительно RUB.
 * Позже можно заменить на API-провайдер без изменения интерфейса.
 */
@Singleton
class LocalExchangeRateProvider @Inject constructor() : ExchangeRateProvider {

    // Курсы относительно RUB (1 unit = X RUB)
    private val ratesToRUB = mapOf(
        "RUB" to 1.0,
        "USD" to 90.0,
        "EUR" to 100.0,
        "GBP" to 115.0
    )

    override fun getRate(fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return 1.0
        val fromRate = ratesToRUB[fromCurrency] ?: 1.0
        val toRate = ratesToRUB[toCurrency] ?: 1.0
        return fromRate / toRate
    }
}
