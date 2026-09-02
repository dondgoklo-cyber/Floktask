package com.taskmanager.domain.finance

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Провайдер курсов валют.
 * Архитектура позволяет подключить реальные API без изменений Finance core.
 */
interface ExchangeRateProvider {
    /**
     * Возвращает курс конвертации из [fromCurrency] в [toCurrency].
     * Например: USD → RUB = 90.0
     */
    fun getRate(fromCurrency: String, toCurrency: String): BigDecimal

    /**
     * Конвертирует [amount] из [fromCurrency] в [toCurrency].
     */
    fun convert(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal {
        if (fromCurrency == toCurrency) return amount
        return amount.multiply(getRate(fromCurrency, toCurrency)).setScale(2, RoundingMode.HALF_EVEN)
    }
}
