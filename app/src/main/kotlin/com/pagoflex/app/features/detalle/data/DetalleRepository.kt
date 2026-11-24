package com.pagoflex.app.features.detalle.data

import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.detalle.data.remote.DetalleApi
import retrofit2.create
import java.time.LocalDate

/** Consulta un compromiso del usuario autenticado; pagabilidad y total son autoritativos. */
class DetalleRepository(@Suppress("UNUSED_PARAMETER") db: LocalDatabase? = null) {
    private val api by lazy { RetrofitClient.retrofit.create<DetalleApi>() }

    suspend fun obtenerDetalle(folio: String): MiCompromisoResponse = api.detalle(folio)

    // ---------- compatibilidad con pago local ----------
    fun calcularRecargo(detalle: DetalleCompromisoRow, fechaActual: LocalDate): Int {
        // 1. aplicar el recargo de la empresa si la cuota pendiente ya vencio
        return if (detalle.estado.equals("pendiente", ignoreCase = true) &&
            detalle.fechaVencimiento.isBefore(fechaActual)
        ) detalle.recargoEmpresa else detalle.recargoAplicado
    }

    fun calcularTotal(detalle: DetalleCompromisoRow, fechaActual: LocalDate): Long =
        detalle.monto.toLong() + calcularRecargo(detalle, fechaActual)
}
