package com.pagoflex.app.features.reconocimientos.data

import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.reconocimientos.data.remote.ReconocimientosApi
import retrofit2.create
import java.time.OffsetDateTime

class ReconocimientosRepository(
    private val api: ReconocimientosApi = RetrofitClient.retrofit.create<ReconocimientosApi>()
) {
    // el backend evalúa y devuelve los logros del usuario autenticado
    suspend fun obtenerReconocimientos(): List<ReconocimientoObtenidoRow> =
        api.obtener().map { reconocimiento ->
            ReconocimientoObtenidoRow(
                nombre = reconocimiento.nombre,
                descripcion = reconocimiento.descripcion,
                fechaObtencion = OffsetDateTime.parse(reconocimiento.fechaObtencion).toLocalDateTime(),
                motivo = reconocimiento.motivo
            )
        }
}
