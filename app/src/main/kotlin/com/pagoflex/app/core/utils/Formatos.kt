package com.pagoflex.app.core.utils

import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// formatos para mostrar montos, porcentajes y fechas en pantalla (RNF-02)

private val localeChile: Locale = Locale.forLanguageTag("es-CL")
private val formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy")
private val formatoFechaHora = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

// 520000 -> "$ 520.000"
fun Long.aPesos(): String = "$ " + NumberFormat.getNumberInstance(localeChile).format(this)

// 88.1 -> "88,1%"
fun Double.aPorcentaje(): String =
    NumberFormat.getNumberInstance(localeChile).apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }.format(this) + "%"

fun LocalDate.aTexto(): String = format(formatoFecha)

fun LocalDateTime.aTexto(): String = format(formatoFechaHora)

// 2026-09 -> "Septiembre 2026"
fun YearMonth.aTexto(): String =
    month.getDisplayName(TextStyle.FULL, localeChile).replaceFirstChar { it.uppercase() } + " $year"
