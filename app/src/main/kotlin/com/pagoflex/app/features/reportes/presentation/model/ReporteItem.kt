package com.pagoflex.app.features.reportes.presentation.model

import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.LocalDate
import java.time.LocalDateTime

// reporte con lo necesario para la bandeja, el detalle y la ayuda del usuario
data class ReporteItem(
    val numero: String,
    val motivo: MotivoReporte,
    val comentario: String?,
    val fechaCreacion: LocalDateTime,
    val estado: EstadoReporte,
    val agente: String?,
    val fechaToma: LocalDateTime?,
    val respuesta: String?,
    val fechaResolucion: LocalDateTime?,
    val folio: String,
    val concepto: String,
    val textoCuota: String?,
    val total: Long,
    val fechaVencimiento: LocalDate,
    val estadoCompromiso: EstadoCompromiso,
    val empresa: String,
    val usuario: String,
    val correo: String,
    val telefono: String?,
    val diasHabiles: Int,
    // más de 2 días hábiles abierto (RN-23)
    val atrasado: Boolean,
)

// compromiso del usuario que se puede reportar o que ya está en revisión
data class CompromisoReportable(
    val folio: String,
    val empresa: String,
    val concepto: String,
    val textoCuota: String?,
    val total: Long,
    val fechaVencimiento: LocalDate,
    val estado: EstadoCompromiso,
)
