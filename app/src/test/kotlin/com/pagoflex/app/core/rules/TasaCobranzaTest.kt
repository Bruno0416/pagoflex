package com.pagoflex.app.core.rules

import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

// tasa de cobranza del ejecutivo (RN-24): pagados / (total - anulados) * 100
class TasaCobranzaTest {

    private val hoy = LocalDate.of(2026, 10, 6)
    private val septiembre = YearMonth.of(2026, 9)
    private val vence = LocalDate.of(2026, 9, 30)

    @Test
    fun `los anulados no cuentan para la tasa`() {
        // 1. preparar: 3 pagados, 1 anulado y 1 vencido
        val items = List(3) { ItemCobranza(vence, EstadoCompromiso.PAGADO, 100_000) } +
            ItemCobranza(vence, EstadoCompromiso.ANULADO, null) +
            ItemCobranza(vence, EstadoCompromiso.VENCIDO, null)

        // 2. ejecutar
        val resumen = CalculoCobranza.calcularResumen(items, septiembre, hoy)

        // 3. verificar: 3 de 4 = 75 %
        assertEquals(75.0, resumen.tasaCobranza!!, 0.0)
        assertEquals(300_000L, resumen.montoRecaudado)
    }

    @Test
    fun `si todo el mes esta anulado no hay tasa`() {
        val items = List(2) { ItemCobranza(vence, EstadoCompromiso.ANULADO, null) }

        val resumen = CalculoCobranza.calcularResumen(items, septiembre, hoy)

        assertNull(resumen.tasaCobranza)
    }
}
