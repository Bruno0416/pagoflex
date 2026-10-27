package com.pagoflex.app.features.cumplimiento.data

import com.pagoflex.app.core.data.local.LocalDatabase
import java.time.LocalDate

class CumplimientoRepository(private val db: LocalDatabase) {

    suspend fun obtenerCumplimiento(
        usuarioId: Int,
        fechaActual: LocalDate = LocalDate.now()
    ): ResultadoCumplimiento {
        // 1. consultar compromisos y pagos del usuario
        val inicio = fechaActual.minusMonths(12)
        val elegibles = db.compromisoDao().obtenerCompromisosConPago(usuarioId)
            .filter { registro ->
                !registro.fechaVencimiento.isBefore(inicio) &&
                    (registro.fechaVencimiento.isBefore(fechaActual) ||
                        (registro.fechaVencimiento == fechaActual && registro.fechaPago != null)) &&
                    registro.estado.trim().lowercase() !in setOf(
                        "anulado", "en revisión", "en revision"
                    )
            }

        // 2. contar pagos realizados dentro de plazo
        val aTiempo = elegibles.count { registro ->
            registro.fechaPago?.toLocalDate()?.let { fechaPago ->
                !fechaPago.isAfter(registro.fechaVencimiento)
            } == true
        }

        // 3. informar historial insuficiente sin porcentaje
        if (elegibles.size < 3) {
            return ResultadoCumplimiento(
                totalElegibles = elegibles.size,
                pagadosATiempo = aTiempo
            )
        }

        // 4. calcular categoria sin redondear el porcentaje
        val porcentaje = aTiempo * 100.0 / elegibles.size
        val categoria = when {
            porcentaje >= 95.0 -> CategoriaCumplimiento.EJEMPLAR
            porcentaje >= 80.0 -> CategoriaCumplimiento.AL_DIA
            else -> CategoriaCumplimiento.EN_CAMINO
        }
        return ResultadoCumplimiento(
            totalElegibles = elegibles.size,
            pagadosATiempo = aTiempo,
            porcentaje = porcentaje,
            categoria = categoria
        )
    }
}
