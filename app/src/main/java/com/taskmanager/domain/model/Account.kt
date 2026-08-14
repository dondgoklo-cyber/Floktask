package com.taskmanager.domain.model

data class Account(
    val id: Long? = null,
    val name: String,
    val initialBalance: Double = 0.0,
    val currency: String = "RUB"
) {
    /** Текущий баланс рассчитывается из операций, но initialBalance — стартовая точка. */
    val currencySymbol: String get() = when (currency) {
        "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }
}
