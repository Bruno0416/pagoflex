package com.pagoflex.app.features.reportes.presentation.model

import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoReporte

// ayuda del usuario: sus reportes y sus cobros vigentes (HU-06)
data class MisReportesUiState(
    val cargando: Boolean = true,
    val reportes: List<ReporteItem> = emptyList(),
    val compromisos: List<CompromisoReportable> = emptyList(),

    // errores
    val error: Int? = null,
)

// formulario para reportar un problema (RF-09)
data class ReportarProblemaUiState(
    val cargando: Boolean = true,
    val compromiso: CompromisoReportable? = null,
    // si ya existe uno se muestra en vez del formulario (HU-06 CA-2)
    val reporteActivo: ReporteItem? = null,
    val motivo: MotivoReporte? = null,
    val comentario: String = "",
    val enviando: Boolean = false,
    val recienEnviado: Boolean = false,

    // errores
    val error: Int? = null,
)

// bandeja del agente (RF-14)
data class BandejaReportesUiState(
    val cargando: Boolean = true,
    val nombreAgente: String = "",
    val inicialesAgente: String = "",
    val avatarAgente: ByteArray? = null,
    // nulo muestra todos
    val filtro: EstadoReporte? = EstadoReporte.ABIERTO,
    val reportes: List<ReporteItem> = emptyList(),
    val cantidadPorEstado: Map<EstadoReporte, Int> = emptyMap(),
    val atrasados: Int = 0,

    // errores
    val error: Int? = null,
)

// detalle del reporte para tomar y resolver (RF-15)
data class DetalleReporteUiState(
    val cargando: Boolean = true,
    val reporte: ReporteItem? = null,
    val respuesta: String = "",
    val guardando: Boolean = false,

    // mensajes y errores
    val mensaje: Int? = null,
    val error: Int? = null,
)
