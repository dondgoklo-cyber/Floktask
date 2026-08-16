package com.taskmanager.presentation.screens.finance

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
fun formatMoney(amount: Double, currency: String = "RUB"): String {
    val symbol = currencySymbol(currency)
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(kotlin.math.abs(amount)).replace(",", " ")
    val sign = if (amount < 0) "-" else ""
    return "$sign$formatted $symbol"
}

/** Форматирует сумму с + знаком для доходов: 180000.0 → "+180 000 ₽" */
fun formatSignedMoney(amount: Double, currency: String = "RUB"): String {
    val symbol = currencySymbol(currency)
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(kotlin.math.abs(amount)).replace(",", " ")
    val sign = if (amount >= 0) "+" else "-"
    return "$sign$formatted $symbol"
}

/**
 * Конвертирует сумму из одной валюты в другую используя ExchangeRateProvider.
 */
fun convertMoney(amount: Double, fromCurrency: String, toCurrency: String, rate: Double): Double {
    if (fromCurrency == toCurrency) return amount
    return amount * rate
}
