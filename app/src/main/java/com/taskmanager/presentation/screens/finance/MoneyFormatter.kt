package com.taskmanager.presentation.screens.finance

import java.text.DecimalFormat

/** Форматирует сумму: 125400.0 → "125 400 ₽" */
fun formatMoney(amount: Double, currency: String = "RUB"): String {
    val symbol = when (currency) {
        "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(kotlin.math.abs(amount)).replace(",", " ")
    val sign = if (amount < 0) "-" else ""
    return "$sign$formatted $symbol"
}

/** Форматирует сумму с + знаком для доходов: 180000.0 → "+180 000 ₽" */
fun formatSignedMoney(amount: Double, currency: String = "RUB"): String {
    val symbol = when (currency) {
        "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(kotlin.math.abs(amount)).replace(",", " ")
    val sign = if (amount >= 0) "+" else "-"
    return "$sign$formatted $symbol"
}
