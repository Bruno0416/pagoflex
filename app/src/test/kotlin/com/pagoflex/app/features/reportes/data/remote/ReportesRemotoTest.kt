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
    fun `reportar devuelve el numero nuevo y manda el motivo`() = runBlocking {
        val resultado = remoto.reportar("CP-2026-0107", MotivoReporte.YA_PAGUE, "  ")

        assertEquals(ResultadoReporte.Creado("REP-0013"), resultado)
        assertEquals("YA_PAGUE", soporte.ultimoReporte?.motivo)
        // un comentario en blanco no se manda
        assertNull(soporte.ultimoReporte?.comentario)
    }

    @Test
    fun `si ya hay un reporte abierto se muestra ese (HU-06 CA-2)`() = runBlocking {
        soporte.codigoError = 409
        soporte.detalleError = "Ya tienes un reporte abierto de este cobro: REP-0012"

        val resultado = remoto.reportar("CP-2026-0106", MotivoReporte.MONTO_INCORRECTO, "")

        assertEquals(ResultadoReporte.YaExiste("REP-0012"), resultado)
    }

    @Test
    fun `un cobro pagado no necesita reporte (409 sin numero)`() = runBlocking {
        soporte.codigoError = 409
        soporte.detalleError = "El cobro ya está pagado"

        val resultado = remoto.reportar("CP-2026-0104", MotivoReporte.YA_PAGUE, "")

        assertEquals(ResultadoReporte.Error(R.string.rep_error_no_necesita), resultado)
    }

    @Test
    fun `la bandeja convierte estados y cuenta los dias habiles`() = runBlocking {
        val bandeja = remoto.bandeja(hoy)

        assertEquals(2, bandeja.size)
        assertEquals(EstadoReporte.ABIERTO, bandeja[0].estado)
        assertEquals("14/36", bandeja[0].textoCuota)
        // del lunes 05-10 al martes 06-10 hay un día hábil
        assertEquals(1, bandeja[0].diasHabiles)
    }

    @Test
    fun `otro agente no puede resolver un reporte que no tomo (403)`() = runBlocking {
        soporte.codigoError = 403

        assertEquals(R.string.per_otro_agente, remoto.resolver("REP-0012", "Revisado"))
    }

    @Test
    fun `sin conexion el usuario ve el mensaje del servidor`() = runBlocking {
        soporte.sinConexion = true

        val resultado = remoto.reportar("CP-2026-0107", MotivoReporte.OTRO, "Me cobraron dos veces")

        assertEquals(ResultadoReporte.Error(R.string.api_error_conexion), resultado)
    }

    @Test
    fun `tomar un reporte lo asigna y empieza la atencion`() = runBlocking {
        val error = remoto.tomar("REP-0012")

        assertNull(error)
        assertEquals(listOf("asignar", "atender"), soporte.pasos)
    }

    @Test
    fun `asignado y en atencion se muestran como en gestion`() = runBlocking {
        soporte.reportes = listOf(
            FakeSoporteApi.reporteDto("REP-0013", "ASIGNADO", fecha = "2026-10-05T00:00:00"),
            FakeSoporteApi.reporteDto("REP-0014", "EN_ATENCION", fecha = "2026-10-05T00:00:00"),
        )

        val bandeja = remoto.bandeja(hoy)

        assertTrue(bandeja.all { it.estado == EstadoReporte.EN_GESTION })
    }

    @Test
    fun `si solo estaba asignado primero inicia la atencion y luego resuelve`() = runBlocking {
        soporte.reportes = listOf(FakeSoporteApi.reporteDto("REP-0013", "ASIGNADO", fecha = "2026-10-05T00:00:00"))

        val error = remoto.resolver("REP-0013", "Revisado con la empresa")

        assertNull(error)
        assertEquals(listOf("atender", "resolver"), soporte.pasos)
    }

    @Test
    fun `los compromisos del usuario vienen de cobranza`() = runBlocking {
        val compromisos = remoto.compromisosDelUsuario()

        assertTrue(compromisos.any { it.folio == "CP-2026-0107" })
        assertEquals("Altos del Maipo", compromisos.first().empresa)
    }
}
