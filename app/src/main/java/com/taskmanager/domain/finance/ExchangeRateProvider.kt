package com.taskmanager.domain.finance

/**
 * Провайдер курсов валют.
 * Архитектура позволяет позже подключить реальный API без переписывания Finance core.
 */
interface ExchangeRateProvider {
    /**
     * Возвращает курс конвертации из [fromCurrency] в [toCurrency].
     * Например: USD → RUB = 90.0
     */
    fun getRate(fromCurrency: String, toCurrency: String): Double

    /**
     * Конвертирует [amount] из [fromCurrency] в [toCurrency].
     */
    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return amount
        return amount * getRate(fromCurrency, toCurrency)
    }
}
