package com.pagoflex.app.features.detalle.data

import com.pagoflex.app.core.data.local.LocalDatabase
import java.time.LocalDate

class DetalleRepository(private val db: LocalDatabase) {

    suspend fun obtenerDetalle(
        folio: String,
        usuarioId: Int
    ): DetalleCompromisoRow? = db.compromisoDao().obtenerDetalle(folio, usuarioId)


    // ---------- helpers ----------

    // ---------- estado actual ----------
    fun obtenerEstadoActual(
        detalle: DetalleCompromisoRow,
        fechaActual: LocalDate
    ): String {
        val estado = detalle.estado.trim().lowercase()

        return if (
            estado == "pendiente" &&
            detalle.fechaVencimiento.isBefore(fechaActual)
        ) {
            "Vencido"
        } else {
            detalle.estado
        }
    }

    // ---------- días de atraso ----------
    fun calcularDiasAtraso(
        detalle: DetalleCompromisoRow,
        fechaActual: LocalDate
    ): Long {
        if (obtenerEstadoActual(detalle, fechaActual) != "Vencido") {
            return 0L
        }

        return (
                fechaActual.toEpochDay() -
                        detalle.fechaVencimiento.toEpochDay()
                ).coerceAtLeast(0L)
    }

    // ---------- recargo ----------
    fun calcularRecargo(
        detalle: DetalleCompromisoRow,
        fechaActual: LocalDate
    ): Int {
        val estadoGuardado = detalle.estado.trim().lowercase()

        return if (
            estadoGuardado == "pendiente" &&
            detalle.fechaVencimiento.isBefore(fechaActual)
        ) {
            detalle.recargoEmpresa
        } else {
            detalle.recargoAplicado
        }
    }

    // ---------- total del compromiso ----------
    fun calcularTotal(
        detalle: DetalleCompromisoRow,
        fechaActual: LocalDate
    ): Long {
        return detalle.monto.toLong() +
                calcularRecargo(detalle, fechaActual)
    }
}
