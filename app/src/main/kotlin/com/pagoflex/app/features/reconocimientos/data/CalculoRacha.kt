package com.pagoflex.app.features.reconocimientos.data

import java.time.LocalDate
import java.time.YearMonth

internal object CalculoRacha {
    fun buscarTresMeses(
        compromisos: List<CompromisoPagoRow>,
        hoy: LocalDate
    ): List<YearMonth>? {
        // 1. agrupar los compromisos no anulados de meses cerrados
        val mesActual = YearMonth.from(hoy)
        val porMes = compromisos
            .filter { YearMonth.from(it.fechaVencimiento) < mesActual &&
                it.estado.trim().lowercase() != "anulado" }
            .groupBy { YearMonth.from(it.fechaVencimiento) }

        // 2. reunir meses consecutivos con todos los pagos a tiempo
        var consecutivos = emptyList<YearMonth>()
        for ((mes, registros) in porMes.toSortedMap()) {
            val valido = registros.all { registro ->
                registro.estado.trim().lowercase() !in setOf("en revisión", "en revision") &&
                    registro.fechaPago?.toLocalDate()?.let { !it.isAfter(registro.fechaVencimiento) } == true
            }
            if (!valido) {
                consecutivos = emptyList()
                continue
            }
            consecutivos = if (consecutivos.lastOrNull() == mes.minusMonths(1)) {
                consecutivos + mes
            } else {
                listOf(mes)
            }
            if (consecutivos.size == 3) return consecutivos
        }
        return null
    }
}
