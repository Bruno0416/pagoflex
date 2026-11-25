package com.pagoflex.app.features.cobranza.data.remote

import com.pagoflex.app.R
import com.pagoflex.app.features.cobranza.data.ResultadoRegistro
import com.pagoflex.app.features.cobranza.presentation.model.FormularioCompromiso
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

// cobranza del ejecutivo contra una api falsa: preparar, ejecutar y verificar
class CobranzaRemotoTest {

    private lateinit var api: FakeCobranzaApi
    private lateinit var remoto: CobranzaRemoto

    @Before
    fun preparar() {
        api = FakeCobranzaApi()
        remoto = CobranzaRemoto(api)
    }

    private fun formulario(rut: String = "12.476.318-5") = FormularioCompromiso(
        rut = rut,
        concepto = "Cuota pie departamento",
        monto = "310000",
        vencimiento = "30-11-2026",
        numeroCuota = "16",
        totalCuotas = "36",
    )

    @Test
    fun `registrar devuelve el folio y no manda datos de persona si ya existe`() = runBlocking {
        val resultado = remoto.registrar(formulario(), personaNueva = false)

        assertTrue(resultado is ResultadoRegistro.Registrado)
        assertEquals("CP-2026-0001", (resultado as ResultadoRegistro.Registrado).compromiso.folio)
        assertEquals(310_000L, api.ultimoRegistro?.monto)
        assertEquals("2026-11-30", api.ultimoRegistro?.fechaVencimiento)
        assertNull(api.ultimoRegistro?.nombres)
    }

    @Test
    fun `compromiso duplicado muestra el mensaje de duplicado (409)`() = runBlocking {
        api.codigoAlRegistrar = 409

        val resultado = remoto.registrar(formulario(), personaNueva = false)

        assertEquals(R.string.cob_error_duplicado, (resultado as ResultadoRegistro.ConErrores).errorGeneral)
    }

}
