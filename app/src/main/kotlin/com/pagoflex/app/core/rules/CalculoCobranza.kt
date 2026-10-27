package com.pagoflex.app.core.rules

import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth

// datos mínimos de un compromiso para calcular la cobranza
data class ItemCobranza(
    val fechaVencimiento: LocalDate,
    val estado: EstadoCompromiso,
    val montoPagado: Long?,
)

data class ResumenCobranza(
    val mes: YearMonth,
    val totalCompromisos: Int = 0,
    val anulados: Int = 0,
    val pagados: Int = 0,
    val pendientes: Int = 0,
    // vencidos sin pagar, incluye los que están en revisión
    val vencidos: Int = 0,
    val enRevision: Int = 0,
    val montoRecaudado: Long = 0L,
    // nulo cuando no hay compromisos que considerar
    val tasaCobranza: Double? = null,
)

// tasa de cobranza del mes (RN-24): pagados / (compromisos del mes - anulados) * 100
// ejemplo del anexo: 120 compromisos, 2 anulados y 104 pagados -> 104 / 118 = 88,1%
object CalculoCobranza {

    fun calcularResumen(
        compromisos: List<ItemCobranza>,
        mes: YearMonth,
        hoy: LocalDate,
    ): ResumenCobranza {
        // 1. compromisos con vencimiento en el mes
        val delMes = compromisos.filter { YearMonth.from(it.fechaVencimiento) == mes }

        // 2. contar por estado
        val anulados = delMes.count { it.estado == EstadoCompromiso.ANULADO }
        val pagados = delMes.count { it.estado == EstadoCompromiso.PAGADO }
        val sinPagar = delMes.filter {
            it.estado != EstadoCompromiso.PAGADO && it.estado != EstadoCompromiso.ANULADO
        }
        val vencidos = sinPagar.count { hoy.isAfter(it.fechaVencimiento) }
        val considerados = delMes.size - anulados

        // 3. armar resumen; lo recaudado incluye los recargos pagados
        return ResumenCobranza(
            mes = mes,
            totalCompromisos = delMes.size,
            anulados = anulados,
            pagados = pagados,
            pendientes = sinPagar.size - vencidos,
            vencidos = vencidos,
            enRevision = delMes.count { it.estado == EstadoCompromiso.EN_REVISION },
            montoRecaudado = delMes.sumOf { it.montoPagado ?: 0L },
            tasaCobranza = if (considerados > 0) porcentaje(pagados, considerados) else null,
        )
    }

    // redondeo con BigDecimal para que 88,135 quede en 88,1
    private fun porcentaje(parte: Int, total: Int): Double =
        BigDecimal(parte * 100.0 / total)
            .setScale(1, RoundingMode.HALF_UP)
            .toDouble()
}
