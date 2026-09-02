package com.taskmanager.presentation.screens.finance

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/** Поддерживаемые валюты в приложении. */
val SUPPORTED_CURRENCIES = listOf("RUB", "USD", "EUR", "GBP")

/** Возвращает символ валюты. */
fun currencySymbol(currency: String): String = when (currency) {
    "RUB" -> "₽"
    "USD" -> "$"
    "EUR" -> "€"
    "GBP" -> "£"
    else -> currency
}

/** Форматирует сумму: 125400.0 → "125 400 ₽" */
fun formatMoney(amount: BigDecimal, currency: String = "RUB"): String {
    val symbol = currencySymbol(currency)
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(amount.abs()).replace(",", " ")
    val sign = if (amount < BigDecimal.ZERO) "-" else ""
    return "$sign$formatted $symbol"
}

/** Форматирует сумму с + знаком для доходов: 180000.0 → "+180 000 ₽" */
fun formatSignedMoney(amount: BigDecimal, currency: String = "RUB"): String {
    val symbol = currencySymbol(currency)
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(amount.abs()).replace(",", " ")
    val sign = if (amount >= BigDecimal.ZERO) "+" else "-"
    return "$sign$formatted $symbol"
}

/**
 * Конвертирует сумму из одной валюты в другую, используя ExchangeRateProvider.
 */
fun convertMoney(amount: BigDecimal, fromCurrency: String, toCurrency: String, rate: BigDecimal): BigDecimal {
    if (fromCurrency == toCurrency) return amount
    return amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN)
}
