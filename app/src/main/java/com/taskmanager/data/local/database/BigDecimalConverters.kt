package com.taskmanager.data.local.database

import androidx.room.TypeConverter
import java.math.BigDecimal

/**
 * Type converters for BigDecimal to work with Room database.
 * Room doesn't support BigDecimal natively, so we store it as String.
 */
object BigDecimalConverters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toPlainString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }
}
