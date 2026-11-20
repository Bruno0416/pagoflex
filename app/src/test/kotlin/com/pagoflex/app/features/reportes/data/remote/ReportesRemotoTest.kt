package com.pagoflex.app.features.reportes.data.remote

import com.pagoflex.app.R
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.features.cobranza.data.remote.FakeCobranzaApi
import com.pagoflex.app.features.reportes.data.ResultadoReporte
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

// reportes del usuario y bandeja del agente contra apis falsas (RN-09, RN-23)
class ReportesRemotoTest {

    private val hoy = LocalDate.of(2026, 10, 6)

    private lateinit var soporte: FakeSoporteApi
    private lateinit var remoto: ReportesRemoto

    @Before
    fun preparar() {
        soporte = FakeSoporteApi()
        remoto = ReportesRemoto(soporte, FakeCobranzaApi())
    }

    @Test
    fun `si ya hay un reporte abierto se muestra ese (HU-06 CA-2)`() = runBlocking {
        soporte.codigoError = 409
        soporte.detalleError = "Ya tienes un reporte abierto de este cobro: REP-0012"

        val resultado = remoto.reportar("CP-2026-0106", MotivoReporte.MONTO_INCORRECTO, "")

        assertEquals(ResultadoReporte.YaExiste("REP-0012"), resultado)
    }

    @Test
    fun `tomar un reporte lo asigna y empieza la atencion`() = runBlocking {
        val error = remoto.tomar("REP-0012")

        assertNull(error)
        assertEquals(listOf("asignar", "atender"), soporte.pasos)
    }

}
