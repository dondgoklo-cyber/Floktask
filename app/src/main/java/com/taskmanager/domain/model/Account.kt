package com.taskmanager.domain.model

import java.math.BigDecimal

data class Account(
    val id: Long? = null,
    val name: String,
    val initialBalance: BigDecimal = BigDecimal.ZERO,
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
