package com.pagoflex.app.core.rules

import com.pagoflex.app.R
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class ReglasTest {

    private val hoy = LocalDate.of(2026, 10, 6)

    // ---------- compromisos ----------

    @Test
    fun `vence a las 2359 y pasa a vencido al dia siguiente (RN-02)`() {
        val vence = LocalDate.of(2026, 10, 5)
        assertEquals(EstadoCompromiso.PENDIENTE, ReglasCompromiso.estadoSegunFecha(vence, vence))
        assertEquals(EstadoCompromiso.VENCIDO, ReglasCompromiso.estadoSegunFecha(vence, vence.plusDays(1)))
    }

    @Test
    fun `un pendiente guardado que ya vencio se ve como vencido con recargo (RN-03)`() {
        val vence = LocalDate.of(2026, 9, 30)
        val estado = ReglasCompromiso.estadoEfectivo(EstadoCompromiso.PENDIENTE, vence, hoy)

        assertEquals(EstadoCompromiso.VENCIDO, estado)
        assertEquals(8_500, ReglasCompromiso.recargoEfectivo(estado, 0, 8_500))
        // un pagado no cambia aunque haya pasado la fecha
        assertEquals(EstadoCompromiso.PAGADO, ReglasCompromiso.estadoEfectivo(EstadoCompromiso.PAGADO, vence, hoy))
    }

    @Test
    fun `dias de atraso de la cuota 14 de Luis son 6 (HU-02 CA-2)`() {
        assertEquals(6L, ReglasCompromiso.diasDeAtraso(LocalDate.of(2026, 9, 30), hoy))
    }

    @Test
    fun `un compromiso pagado no se puede anular (HU-11 CA-4)`() {
        val error = ReglasCompromiso.validarAnulacion(
            EstadoCompromiso.PAGADO, 6, 6, MotivoAnulacion.ERROR_REGISTRO, ""
        )
        assertEquals(R.string.cob_error_anular_pagado, error)
    }

    @Test
    fun `un ejecutivo no anula compromisos de otra empresa (RN-21)`() {
        val error = ReglasCompromiso.validarAnulacion(
            EstadoCompromiso.PENDIENTE, 1, 6, MotivoAnulacion.ERROR_REGISTRO, ""
        )
        assertEquals(R.string.cob_error_otra_empresa, error)
    }

    @Test
    fun `motivo otro exige comentario al anular`() {
        assertEquals(
            R.string.val_comentario_otro,
            ReglasCompromiso.validarAnulacion(EstadoCompromiso.EN_REVISION, 7, 7, MotivoAnulacion.OTRO, "")
        )
        assertNull(
            ReglasCompromiso.validarAnulacion(EstadoCompromiso.EN_REVISION, 7, 7, MotivoAnulacion.OTRO, "Cambio de contrato")
        )
    }

    @Test
    fun `siguiente folio`() {
        val folios = listOf("CP-2026-0101", "CP-2026-0319", "CP-2025-0900")
        assertEquals("CP-2026-0320", ReglasCompromiso.siguienteFolio(folios, 2026))
        assertEquals("CP-2027-0001", ReglasCompromiso.siguienteFolio(folios, 2027))
    }

    // ---------- reportes ----------

    @Test
    fun `dias habiles no cuentan fin de semana`() {
        // 30-09 (miércoles) al 06-10 (martes): jue, vie, lun, mar
        assertEquals(4, ReglasReporte.diasHabilesEntre(LocalDate.of(2026, 9, 30), hoy))
    }

    @Test
    fun `reporte abierto con mas de 2 dias habiles esta atrasado (RN-23)`() {
        assertTrue(ReglasReporte.estaAtrasado(EstadoReporte.ABIERTO, LocalDate.of(2026, 9, 30), hoy))
        assertFalse(ReglasReporte.estaAtrasado(EstadoReporte.ABIERTO, LocalDate.of(2026, 10, 5), hoy))
        // en gestión ya no cuenta como atrasado
        assertFalse(ReglasReporte.estaAtrasado(EstadoReporte.EN_GESTION, LocalDate.of(2026, 9, 30), hoy))
    }

    @Test
    fun `al resolver vuelve segun la fecha o queda anulado (RN-09)`() {
        val vence = LocalDate.of(2026, 9, 30)
        assertEquals(EstadoCompromiso.VENCIDO, ReglasReporte.estadoAlResolver(EstadoCompromiso.EN_REVISION, vence, hoy))
        assertEquals(EstadoCompromiso.ANULADO, ReglasReporte.estadoAlResolver(EstadoCompromiso.ANULADO, vence, hoy))
    }

    @Test
    fun `solo se reporta lo que aun se debe`() {
        assertTrue(ReglasReporte.puedeReportarse(EstadoCompromiso.VENCIDO))
        assertFalse(ReglasReporte.puedeReportarse(EstadoCompromiso.PAGADO))
        assertFalse(ReglasReporte.puedeReportarse(EstadoCompromiso.EN_REVISION))
    }

    @Test
    fun `siguiente numero de reporte`() {
        assertEquals("REP-0013", ReglasReporte.siguienteNumero(listOf("REP-0009", "REP-0012", "REP-0011")))
    }

    @Test
    fun `catalogos se leen por el nombre guardado en la base`() {
        assertEquals(EstadoReporte.EN_GESTION, EstadoReporte.desdeNombre("En gestión"))
        assertEquals(MotivoAnulacion.PAGADO_DIRECTO, MotivoAnulacion.desdeNombre("Pagado directamente a la empresa"))
        assertEquals(EstadoCompromiso.EN_REVISION, estadoCompromisoDesdeNombre("En revisión"))
        assertEquals("En revisión", EstadoCompromiso.EN_REVISION.nombre)
    }

    // ---------- cobranza (RN-24) ----------

    @Test
    fun `resumen de septiembre de Altos del Maipo (HU-12 CA-1)`() {
        // 96 a tiempo, 8 con atraso, 2 anulados, 13 vencidos y la 0106 en revisión
        val vence = LocalDate.of(2026, 9, 30)
        val items = List(96) { ItemCobranza(vence, EstadoCompromiso.PAGADO, 310_000) } +
                List(8) { ItemCobranza(vence, EstadoCompromiso.PAGADO, 339_500) } +
                List(2) { ItemCobranza(vence, EstadoCompromiso.ANULADO, null) } +
                List(13) { ItemCobranza(vence, EstadoCompromiso.VENCIDO, null) } +
                ItemCobranza(vence, EstadoCompromiso.EN_REVISION, null)

        val resumen = CalculoCobranza.calcularResumen(items, YearMonth.of(2026, 9), hoy)

        assertEquals(120, resumen.totalCompromisos)
        assertEquals(2, resumen.anulados)
        assertEquals(104, resumen.pagados)
        assertEquals(14, resumen.vencidos)
        assertEquals(32_476_000L, resumen.montoRecaudado)
        assertEquals(88.1, resumen.tasaCobranza!!, 0.0)
    }

    @Test
    fun `mes sin compromisos no tiene tasa`() {
        assertNull(CalculoCobranza.calcularResumen(emptyList(), YearMonth.of(2026, 12), hoy).tasaCobranza)
    }
}
