package com.taskmanager.data.repository

import com.taskmanager.domain.finance.ExchangeRateProvider
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Локальный (mock) провайдер курсов валют.
 * Содержит фиксированные курсы относительно RUB.
 * Позже можно заменить на API-провайдер без изменений интерфейса.
 */
@Singleton
class LocalExchangeRateProvider @Inject constructor() : ExchangeRateProvider {

    // Курсы относительно RUB (1 unit = X RUB)
    private val ratesToRUB = mapOf(
        "RUB" to BigDecimal.ONE,
        "USD" to BigDecimal("90.0"),
        "EUR" to BigDecimal("100.0"),
        "GBP" to BigDecimal("115.0")
    )

    override fun getRate(fromCurrency: String, toCurrency: String): BigDecimal {
        if (fromCurrency == toCurrency) return BigDecimal.ONE
        val fromRate = ratesToRUB[fromCurrency] ?: BigDecimal.ONE
        val toRate = ratesToRUB[toCurrency] ?: BigDecimal.ONE
        return fromRate.divide(toRate, 10, java.math.RoundingMode.HALF_EVEN)
    }
}
