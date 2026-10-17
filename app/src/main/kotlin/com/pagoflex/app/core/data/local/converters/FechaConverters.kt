package com.pagoflex.app.core.data.local.converters

import androidx.room3.ColumnTypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

// convertidores de fecha de java LocalDate -> TEXT SQLite(no tiene tipo de dato para fechas)
class FechaConverters {
    @ColumnTypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @ColumnTypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @ColumnTypeConverter
    fun localDateTimeToString(value: LocalDateTime?): String? = value?.toString()

    @ColumnTypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? = value?.let(LocalDateTime::parse)
}
