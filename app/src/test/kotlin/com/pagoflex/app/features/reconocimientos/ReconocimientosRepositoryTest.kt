package com.pagoflex.app.features.reconocimientos

import com.pagoflex.app.features.reconocimientos.data.ReconocimientosRepository
import com.pagoflex.app.features.reconocimientos.data.remote.ReconocimientosApi
import com.pagoflex.app.features.reconocimientos.data.remote.dto.response.ReconocimientoResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class ReconocimientosRepositoryTest {
    @Test
    fun obtenerReconocimientos_sinLogros_devuelveListaVacia() = runBlocking {
        val api = object : ReconocimientosApi {
            override suspend fun obtener(): List<ReconocimientoResponse> = emptyList()
        }
        val repositorio = ReconocimientosRepository(api)

        assertTrue(repositorio.obtenerReconocimientos().isEmpty())
    }

    @Test
    fun obtenerReconocimientos_conLogros_conservaDatosYFecha() = runBlocking {
        val respuesta = ReconocimientoResponse(
            codigo = "PRIMER_PAGO_A_TIEMPO",
            nombre = "Primer pago a tiempo",
            descripcion = "Tu primer compromiso pagado a tiempo",
            motivo = "Pago CP-2026-0105",
            fechaObtencion = "2026-10-06T10:30:00-03:00"
        )
        val api = object : ReconocimientosApi {
            override suspend fun obtener() = listOf(respuesta)
        }
        val repositorio = ReconocimientosRepository(api)

        val reconocimiento = repositorio.obtenerReconocimientos().single()

        assertEquals(respuesta.nombre, reconocimiento.nombre)
        assertEquals(respuesta.descripcion, reconocimiento.descripcion)
        assertEquals(respuesta.motivo, reconocimiento.motivo)
        assertEquals(LocalDateTime.of(2026, 10, 6, 10, 30), reconocimiento.fechaObtencion)
    }
}
