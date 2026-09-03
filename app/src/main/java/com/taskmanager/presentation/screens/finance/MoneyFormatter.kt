package com.taskmanager.presentation.screens.finance

import com.taskmanager.utils.toDisplayDouble
import java.math.BigDecimal
import java.text.DecimalFormat

/** Поддерживаемые валюты в приложении. */
val SUPPORTED_CURRENCIES = listOf("RUB", "USD", "EUR", "GBP")

/** Возвращает символ валюты. */
fun currencySymbol(currency: String): String = when (currency) {
    "RUB" -> "\u20bd"
    "USD" -> "$"
    "EUR" -> "\u20ac"
    "GBP" -> "\u00a3"
    else -> currency
}

/** Форматирует сумму: 125400.0 → "125 400 \u20bd" */
fun formatMoney(amount: Double, currency: String = "RUB"): String {
    val symbol = currencySymbol(currency)
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(kotlin.math.abs(amount)).replace(",", " ")
    val sign = if (amount < 0) "-" else ""
    return "$sign$formatted $symbol"
}

fun formatMoney(amount: BigDecimal, currency: String = "RUB"): String {
    return formatMoney(amount.toDisplayDouble(), currency)
}

/** Форматирует сумму с знаком: 180000.0 → "+180 000 \u20bd" */
fun formatSignedMoney(amount: Double, currency: String = "RUB"): String {
    val symbol = currencySymbol(currency)
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(kotlin.math.abs(amount)).replace(",", " ")
    val sign = if (amount >= 0) "+" else "-"
    return "$sign$formatted $symbol"
}

fun formatSignedMoney(amount: BigDecimal, currency: String = "RUB"): String {
    return formatSignedMoney(amount.toDisplayDouble(), currency)
}

/**
 * Конвертирует сумму из одной валюты в другую, используя курс из ExchangeRateProvider.
 */
fun convertMoney(amount: Double, fromCurrency: String, toCurrency: String, rate: Double): Double {
    if (fromCurrency == toCurrency) return amount
    return amount * rate
}
