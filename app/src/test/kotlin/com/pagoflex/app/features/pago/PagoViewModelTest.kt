package com.pagoflex.app.features.pago

import com.pagoflex.app.features.pago.data.PagoRepository
import com.pagoflex.app.features.pago.data.MetodoPagoModel
import com.pagoflex.app.features.pago.presentation.viewmodel.PagoViewModel
import com.pagoflex.app.features.pago.presentation.model.PasoPago
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class PagoViewModelTest {
    private lateinit var apiFalsa: FakePagoApi
    private lateinit var repo: PagoRepository
    private lateinit var viewmodel: PagoViewModel

    @Before
    fun preparar() {
        apiFalsa = FakePagoApi()
        repo = PagoRepository(apiFalsa)

        runBlocking {
            viewmodel = PagoViewModel(repo, this)
            viewmodel.iniciarPago("PF-001", "sesion-prueba")
        }

        assertNotNull(viewmodel.uiState.value.resumen)
        assertFalse(viewmodel.uiState.value.cargando)
    }

    @Test
    fun seleccionarMetodo_actualizaMetodoSeleccionado() {
        val estadoInicial = viewmodel.uiState.value
        assertFalse(estadoInicial.cargando)
        assertFalse(estadoInicial.procesando)
        assertNull(estadoInicial.metodo)

        // 2. seleccionar método de pago
        viewmodel.seleccionarMetodo(MetodoPagoModel.DEBITO)
        assertEquals(MetodoPagoModel.DEBITO, viewmodel.uiState.value.metodo)

        // 3. cambiar método de pago
        viewmodel.seleccionarMetodo(MetodoPagoModel.TRANSFERENCIA)
        assertEquals(MetodoPagoModel.TRANSFERENCIA, viewmodel.uiState.value.metodo)
    }

    @Test
    fun avanzarAConfirmacion_sinMetodo_noAvanza() {
        // 1. comprobar estado inicial
        val estadoInicial = viewmodel.uiState.value

        assertFalse(estadoInicial.cargando)
        assertFalse(estadoInicial.procesando)
        assertNotNull(estadoInicial.resumen)
        assertNull(estadoInicial.metodo)
        assertEquals(PasoPago.METODO, estadoInicial.paso)

        // 2. intentar continuar sin método de pago
        viewmodel.avanzarAConfirmacion()

        // 3. comprobar que no avanzó
        val state = viewmodel.uiState.value
        assertEquals(PasoPago.METODO, state.paso)
    }

    @Test
    fun avanzarAConfirmacion_conDatosValidos_avanza() {
        // 1. comprobar estado inicial
        val estadoInicial = viewmodel.uiState.value

        assertFalse(estadoInicial.cargando)
        assertFalse(estadoInicial.procesando)
        assertNotNull(estadoInicial.resumen)
        assertNull(estadoInicial.metodo)
        assertEquals(PasoPago.METODO, estadoInicial.paso)

        // 2. seleccionar metodo
        viewmodel.seleccionarMetodo(MetodoPagoModel.DEBITO)
        assertEquals(MetodoPagoModel.DEBITO, viewmodel.uiState.value.metodo)


        // 3. intentar continuar con metodo de pago válido
        viewmodel.avanzarAConfirmacion()

        // 3. comprobar que avanzó
        val state = viewmodel.uiState.value
        assertEquals(PasoPago.CONFIRMACION, state.paso)
    }
}