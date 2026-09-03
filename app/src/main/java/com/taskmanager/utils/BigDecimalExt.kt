package com.taskmanager.utils

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Конвертации между BigDecimal и Double для UI слоя
 * Domain/Data используют BigDecimal (точная арифметика)
 * UI использует Double (для отображения и анимаций)
 */

fun BigDecimal.toDisplayDouble(): Double = this.toDouble()

fun BigDecimal.toDisplayString(): String = this.setScale(2, RoundingMode.HALF_UP).toPlainString()

fun Double.toMoneyBigDecimal(): BigDecimal = BigDecimal.valueOf(this)

fun Int.toMoneyBigDecimal(): BigDecimal = BigDecimal.valueOf(this.toLong())

fun Long.toMoneyBigDecimal(): BigDecimal = BigDecimal.valueOf(this)

fun String.toMoneyBigDecimal(): BigDecimal = this.toBigDecimalOrNull() ?: BigDecimal.ZERO

// Безопасное деление для финансов
fun BigDecimal.divideSafe(divisor: BigDecimal, scale: Int = 2): BigDecimal {
    if (divisor.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO
    return this.divide(divisor, scale, RoundingMode.HALF_UP)
}

fun BigDecimal.divideSafe(divisor: Int, scale: Int = 2): BigDecimal {
    if (divisor == 0) return BigDecimal.ZERO
    return this.divide(BigDecimal.valueOf(divisor.toLong()), scale, RoundingMode.HALF_UP)
}

// Суммирование BigDecimal коллекций
fun <T> Collection<T>.sumOfBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum = sum.add(selector(element))
    }
    return sum
}
