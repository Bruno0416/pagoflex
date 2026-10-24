package com.pagoflex.app.features.home.data

import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.features.home.presentation.model.CompromisoInicio
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.LocalDate

class HomeRepository(private val db: LocalDatabase) {

    suspend fun obtenerNombre(usuarioId: Int): String? =
        db.usuarioDao().obtenerNombreParaHome(usuarioId)?.substringBefore(" ")

    suspend fun obtenerCompromisos(
        usuarioId: Int,
        fechaActual: LocalDate
    ): List<CompromisoInicio> {
        return db.compromisoDao().obtenerParaHome(usuarioId).map { registro ->
            val estadoGuardado = when (registro.estado.trim().lowercase()) {
                "pendiente" -> EstadoCompromiso.PENDIENTE
                "vencido" -> EstadoCompromiso.VENCIDO
                "pagado" -> EstadoCompromiso.PAGADO
                "en revisión", "en revision" -> EstadoCompromiso.EN_REVISION
                "anulado" -> EstadoCompromiso.ANULADO
                else -> error("Estado de compromiso no reconocido: ${registro.estado}")
            }

            // 1. actualizar estado vencido
            val vencio = registro.fechaVencimiento.isBefore(fechaActual)
            val estado = if (estadoGuardado == EstadoCompromiso.PENDIENTE && vencio) {
                EstadoCompromiso.VENCIDO
            } else {
                estadoGuardado
            }

            // 2. calcular recargo mostrado
            val recargo = if (estado == EstadoCompromiso.VENCIDO) {
                if (estadoGuardado == EstadoCompromiso.PENDIENTE) {
                    registro.recargoEmpresa.toLong()
                } else {
                    registro.recargoAplicado.toLong()
                }
            } else {
                registro.recargoAplicado.toLong()
            }

            CompromisoInicio(
                id = registro.folio,
                organizacion = registro.organizacion,
                siglaOrganizacion = registro.organizacion
                    .split(' ')
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.take(1).uppercase() },
                concepto = registro.concepto,
                numeroCuota = registro.numeroCuota,
                totalCuotas = registro.totalCuotas,
                fechaVencimiento = registro.fechaVencimiento,
                monto = registro.monto.toLong(),
                recargo = recargo,
                estado = estado
            )
        }
    }
}
