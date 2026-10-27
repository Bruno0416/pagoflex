package com.pagoflex.app.features

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoAnulacion
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.data.EjecutivoSesion
import com.pagoflex.app.features.cobranza.data.ResultadoRegistro
import com.pagoflex.app.features.cobranza.presentation.model.CampoCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.FormularioCompromiso
import com.pagoflex.app.features.cobranza.presentation.viewmodel.CompromisosEmpresaViewModel
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.data.ResultadoReporte
import com.pagoflex.app.features.reportes.presentation.viewmodel.BandejaReportesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime

// flujos de agente y ejecutivo con los datos ficticios del anexo 9 sobre una base real
@RunWith(AndroidJUnit4::class)
class FlujosAgenteEjecutivoTest {

    // fecha de referencia del caso
    private val ahora = LocalDateTime.of(2026, 10, 6, 10, 0)
    private val hoy = ahora.toLocalDate()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: LocalDatabase
    private lateinit var cobranza: CobranzaRepository
    private lateinit var reportes: ReportesRepository

    @Before
    fun crearBase() = runBlocking<Unit> {
        db = Room.inMemoryDatabaseBuilder<LocalDatabase>(context)
            .setDriver(AndroidSQLiteDriver())
            .build()
        db.initDB()
        cobranza = CobranzaRepository(db)
        reportes = ReportesRepository(db)
    }

    @After
    fun cerrarBase() {
        db.close()
    }

    // ---------- permisos entre empresas (RN-22) ----------

    @Test
    fun ejecutivoSoloVeLosCompromisosDeSuEmpresa() = runBlocking {
        val paula = ejecutivo(EJ_01)
        val sebastian = ejecutivo(EJ_06)

        val dePaula = cobranza.obtenerCompromisos(paula.idEmpresa!!, hoy).map { it.folio }
        val deSebastian = cobranza.obtenerCompromisos(sebastian.idEmpresa!!, hoy).map { it.folio }

        assertTrue("CP-2026-0106" in dePaula)
        assertFalse("CP-2026-0109" in dePaula)
        assertTrue("CP-2026-0109" in deSebastian)
        assertTrue(dePaula.intersect(deSebastian.toSet()).isEmpty())
    }

    @Test
    fun ejecutivoNoAbreNiAnulaCompromisosDeOtraEmpresa() = runBlocking {
        val paula = ejecutivo(EJ_01)

        // el arancel de Matías es del instituto, no de altos del maipo
        assertNull(cobranza.obtenerDetalle(paula.idEmpresa!!, "CP-2026-0109", hoy))
        assertNull(cobranza.obtenerReporteActivo(paula.idEmpresa!!, "CP-2026-0109"))

        val error = cobranza.anular("CP-2026-0109", paula, MotivoAnulacion.REPACTACION, "", ahora)
        assertNotNull(error)
        assertEquals(EstadoCompromiso.EN_REVISION, estadoParaEjecutivo(EJ_06, "CP-2026-0109"))
    }

    @Test
    fun ejecutivoVeElReporteDeSuEmpresaEnLectura() = runBlocking {
        val sebastian = ejecutivo(EJ_06)

        val reporte = cobranza.obtenerReporteActivo(sebastian.idEmpresa!!, "CP-2026-0109")
        assertEquals("REP-0011", reporte?.numero)
        // no puede gestionarlo
        assertEquals(R.string.per_sin_permiso, reportes.tomar("REP-0011", sebastian.idUsuario, ahora))
    }

    // ---------- permisos por rol (RNF-13) ----------

    @Test
    fun cadaRolSoloHaceLoQueLeCorresponde() = runBlocking {
        val camila = idDe(UF_01)
        val andres = idDe(AG_01)
        val paula = ejecutivo(EJ_01)

        // agente y usuario final no entran a la cobranza
        assertNull(cobranza.obtenerEjecutivo(andres))
        assertNull(cobranza.obtenerEjecutivo(camila))

        // el agente no registra ni anula aunque arme una sesión de ejecutivo
        val falsa = EjecutivoSesion(andres, "Andrés", paula.idEmpresa, paula.empresa)
        val registro = cobranza.registrar(formularioValido(), falsa, ahora)
        assertEquals(R.string.per_sin_permiso, (registro as ResultadoRegistro.ConErrores).errorGeneral)
        assertEquals(
            R.string.per_sin_permiso,
            cobranza.anular("CP-2026-0107", falsa, MotivoAnulacion.REPACTACION, "", ahora)
        )

        // el ejecutivo y el usuario final no ven la bandeja de reportes
        assertTrue(reportes.obtenerTodos(paula.idUsuario, hoy).isEmpty())
        assertTrue(reportes.obtenerTodos(camila, hoy).isEmpty())
        assertNull(reportes.obtenerDetalle("REP-0011", camila, hoy))

        // solo el usuario final reporta problemas
        val reporte = reportes.reportar("CP-2026-0105", andres, MotivoReporte.YA_PAGUE, "", ahora)
        assertEquals(R.string.per_sin_permiso, (reporte as ResultadoReporte.Error).mensaje)
    }

    @Test
    fun agenteInactivoNoTienePermisos() = runBlocking {
        val ricardo = idDe(AG_03)
        assertTrue(reportes.obtenerTodos(ricardo, hoy).isEmpty())
        assertEquals(R.string.per_sin_permiso, reportes.tomar("REP-0011", ricardo, ahora))
    }

    // ---------- registro y anulación (HU-11) ----------

    @Test
    fun registrarCompromisoQuedaPendienteConElEjecutivo() = runBlocking {
        val paula = ejecutivo(EJ_01)

        val resultado = cobranza.registrar(formularioValido(), paula, ahora)
        assertTrue(resultado is ResultadoRegistro.Registrado)
        val folio = (resultado as ResultadoRegistro.Registrado).compromiso.folio

        // se refleja en la lista de la empresa con quién lo registró (RN-20)
        val detalle = cobranza.obtenerDetalle(paula.idEmpresa!!, folio, hoy)!!
        assertEquals(EstadoCompromiso.PENDIENTE, detalle.estado)
        assertTrue(detalle.registradoPor!!.startsWith("Paula"))
        assertEquals(ahora, detalle.fechaRegistro)

        // y también en el home de la persona
        assertTrue(reportes.obtenerCompromisosReportables(idDe(UF_02), hoy).any { it.folio == folio })

        // el mismo compromiso no se registra dos veces
        val repetido = cobranza.registrar(formularioValido(), paula, ahora)
        assertEquals(R.string.cob_error_duplicado, (repetido as ResultadoRegistro.ConErrores).errorGeneral)
    }

    @Test
    fun registroConDatosInvalidosNoGuardaNada() = runBlocking {
        val paula = ejecutivo(EJ_01)
        val antes = cobranza.obtenerCompromisos(paula.idEmpresa!!, hoy).size

        val invalido = formularioValido().copy(
            rut = "12.345.678-9", monto = "0", vencimiento = "01-10-2026",
            numeroCuota = "5", totalCuotas = "3"
        )
        val errores = (cobranza.registrar(invalido, paula, ahora) as ResultadoRegistro.ConErrores).errores

        assertEquals(
            setOf(CampoCompromiso.RUT, CampoCompromiso.MONTO, CampoCompromiso.VENCIMIENTO, CampoCompromiso.CUOTA),
            errores.keys
        )
        assertEquals(antes, cobranza.obtenerCompromisos(paula.idEmpresa!!, hoy).size)
    }

    @Test
    fun anularRegistraEjecutivoMotivoYFecha() = runBlocking {
        val paula = ejecutivo(EJ_01)

        // un pagado nunca se anula (RN-21)
        val pagado = cobranza.obtenerCompromisos(paula.idEmpresa!!, hoy).first { it.estado == EstadoCompromiso.PAGADO }
        assertNotNull(cobranza.anular(pagado.folio, paula, MotivoAnulacion.ERROR_REGISTRO, "", ahora))

        // "otro" pide comentario
        assertNotNull(cobranza.anular("CP-2026-0107", paula, MotivoAnulacion.OTRO, " ", ahora))

        assertNull(cobranza.anular("CP-2026-0107", paula, MotivoAnulacion.REPACTACION, "", ahora))
        val detalle = cobranza.obtenerDetalle(paula.idEmpresa!!, "CP-2026-0107", hoy)!!
        assertEquals(EstadoCompromiso.ANULADO, detalle.estado)
        assertEquals(MotivoAnulacion.REPACTACION, detalle.anulacion?.motivo)
        assertEquals(ahora, detalle.anulacion?.fecha)
        assertTrue(detalle.anulacion!!.ejecutivo!!.startsWith("Paula"))
    }

    // ---------- reportes (HU-06, HU-10) ----------

    @Test
    fun flujoCompletoDeUnReporte() = runBlocking {
        val camila = idDe(UF_01)
        val andres = idDe(AG_01)
        val daniela = idDe(AG_02)

        // 1. camila reporta y el compromiso queda en revisión (RN-09)
        val creado = reportes.reportar("CP-2026-0105", camila, MotivoReporte.MONTO_INCORRECTO, "", ahora)
        val numero = (creado as ResultadoReporte.Creado).numero
        assertEquals(EstadoCompromiso.EN_REVISION, reportes.obtenerCompromisoDeUsuario(camila, "CP-2026-0105", hoy)?.estado)

        // 2. aparece abierto en la bandeja del agente
        assertEquals(EstadoReporte.ABIERTO, reportes.obtenerTodos(andres, hoy).first { it.numero == numero }.estado)

        // 3. andrés lo toma y queda como responsable (RN-23)
        assertNull(reportes.tomar(numero, andres, ahora))
        assertEquals(R.string.rep_error_ya_tomado, reportes.tomar(numero, daniela, ahora))
        val tomado = reportes.obtenerDetalle(numero, andres, hoy)!!
        assertEquals(EstadoReporte.EN_GESTION, tomado.estado)
        assertTrue(tomado.agente!!.startsWith("Andrés"))

        // 4. otra agente no lo resuelve y sin respuesta tampoco se puede
        assertEquals(R.string.per_otro_agente, reportes.resolver(numero, daniela, "Listo", ahora))
        assertEquals(R.string.rep_error_sin_respuesta, reportes.resolver(numero, andres, "  ", ahora))

        // 5. resuelto: el compromiso sale de revisión según su fecha
        assertNull(reportes.resolver(numero, andres, "El monto está correcto según el contrato.", ahora))
        val resuelto = reportes.obtenerDetalle(numero, andres, hoy)!!
        assertEquals(EstadoReporte.RESUELTO, resuelto.estado)
        assertEquals(ahora, resuelto.fechaResolucion)
        assertEquals(EstadoCompromiso.PENDIENTE, reportes.obtenerCompromisoDeUsuario(camila, "CP-2026-0105", hoy)?.estado)

        // 6. camila ve la respuesta en su ayuda
        val suyo = reportes.obtenerReportesDeUsuario(camila, hoy).first { it.numero == numero }
        assertEquals("El monto está correcto según el contrato.", suyo.respuesta)
    }

    @Test
    fun reporteGestionadoPorOtroAgenteNoSePuedeResolver() = runBlocking {
        // REP-0011 lo tomó Daniela
        assertEquals(R.string.per_otro_agente, reportes.resolver("REP-0011", idDe(AG_01), "Listo", ahora))
        assertNull(reportes.resolver("REP-0011", idDe(AG_02), "Confirmamos el pago con el instituto.", ahora))
    }

    // ---------- filtros ----------

    @Test
    fun filtrosDeLaListaDelEjecutivo() = runBlocking {
        val viewModel = CompromisosEmpresaViewModel(cobranza) { LocalDate.of(2026, 9, 15) }
        viewModel.cargar(idDe(EJ_01))
        esperar { !viewModel.uiState.value.cargando }

        // septiembre de altos del maipo: 104 pagados y 2 anulados (HU-12)
        viewModel.filtrar(EstadoCompromiso.PAGADO)
        assertEquals(104, viewModel.uiState.value.compromisos.size)
        assertTrue(viewModel.uiState.value.compromisos.all { it.fechaVencimiento.monthValue == 9 })

        viewModel.filtrar(EstadoCompromiso.ANULADO)
        assertEquals(2, viewModel.uiState.value.compromisos.size)

        // al cambiar de mes se ven los de octubre
        viewModel.filtrar(null)
        viewModel.mesSiguiente()
        assertTrue(viewModel.uiState.value.compromisos.any { it.folio == "CP-2026-0107" })
    }

    @Test
    fun filtrosDeLaBandejaDelAgente() = runBlocking {
        val viewModel = BandejaReportesViewModel(reportes) { hoy }
        viewModel.cargar(idDe(AG_01))
        esperar { !viewModel.uiState.value.cargando }

        viewModel.filtrar(EstadoReporte.EN_GESTION)
        assertTrue(viewModel.uiState.value.reportes.any { it.numero == "REP-0011" })
        assertTrue(viewModel.uiState.value.reportes.all { it.estado == EstadoReporte.EN_GESTION })
    }

    // ---------- persistencia ----------

    @Test
    fun losCambiosSeConservanAlReabrirLaBase() = runBlocking<Unit> {
        val nombre = "flujos_test.db"
        context.deleteDatabase(nombre)

        // 1. registrar en una base en disco y cerrarla
        val enDisco = Room.databaseBuilder<LocalDatabase>(context, nombre).setDriver(AndroidSQLiteDriver()).build()
        enDisco.initDB()
        val repo = CobranzaRepository(enDisco)
        val paula = requireNotNull(repo.obtenerEjecutivo(enDisco.usuarioDao().obtenerPorRut(EJ_01)!!.idUsuario))
        val folio = (repo.registrar(formularioValido(), paula, ahora) as ResultadoRegistro.Registrado).compromiso.folio
        enDisco.close()

        // 2. reabrir y encontrar el compromiso
        val reabierta = Room.databaseBuilder<LocalDatabase>(context, nombre).setDriver(AndroidSQLiteDriver()).build()
        reabierta.initDB()
        assertNotNull(CobranzaRepository(reabierta).obtenerDetalle(paula.idEmpresa!!, folio, hoy))
        reabierta.close()
        context.deleteDatabase(nombre)
    }

    // ---------- helpers ----------

    private suspend fun idDe(rut: String): Int = requireNotNull(db.usuarioDao().obtenerPorRut(rut)).idUsuario

    private suspend fun ejecutivo(rut: String): EjecutivoSesion =
        requireNotNull(cobranza.obtenerEjecutivo(idDe(rut)))

    private suspend fun estadoParaEjecutivo(rut: String, folio: String): EstadoCompromiso? {
        val sesion = ejecutivo(rut)
        return cobranza.obtenerDetalle(sesion.idEmpresa!!, folio, hoy)?.estado
    }

    // luis ya existe, así que no se piden sus datos
    private fun formularioValido() = FormularioCompromiso(
        rut = UF_02,
        concepto = "Gastos comunes noviembre",
        monto = "45000",
        vencimiento = "30-11-2026",
    )

    private suspend fun esperar(condicion: () -> Boolean) = withTimeout(10_000) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        while (!condicion()) delay(50)
    }

    private companion object {
        const val UF_01 = "15.834.207-3"
        const val UF_02 = "12.476.318-5"
        const val AG_01 = "16.248.317-K"
        const val AG_02 = "18.352.091-1"
        const val AG_03 = "13.907.264-2"
        const val EJ_01 = "14.629.853-2"
        const val EJ_06 = "18.930.571-0"
    }
}
