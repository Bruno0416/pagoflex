package com.pagoflex.app.core.rules

import androidx.annotation.StringRes
import com.pagoflex.app.R
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso

// catálogos del mer: `nombre` es el valor guardado en la base y `labelRes` el texto en pantalla

enum class EstadoReporte(val nombre: String, @StringRes val labelRes: Int) {
    ABIERTO("Abierto", R.string.cat_reporte_abierto),
    EN_GESTION("En gestión", R.string.cat_reporte_en_gestion),
    RESUELTO("Resuelto", R.string.cat_reporte_resuelto);

    companion object {
        fun desdeNombre(nombre: String): EstadoReporte =
            entries.firstOrNull { it.nombre == nombre.trim() }
                ?: error("Estado de reporte no reconocido: $nombre")
    }
}

enum class MotivoReporte(val nombre: String, @StringRes val labelRes: Int) {
    YA_PAGUE("Ya pagué por otro medio", R.string.cat_motivo_ya_pague),
    NO_RECONOZCO("No reconozco este cobro", R.string.cat_motivo_no_reconozco),
    MONTO_INCORRECTO("El monto no corresponde", R.string.cat_motivo_monto),
    OTRO("Otro", R.string.cat_motivo_otro);

    companion object {
        fun desdeNombre(nombre: String): MotivoReporte =
            entries.firstOrNull { it.nombre == nombre.trim() }
                ?: error("Motivo de reporte no reconocido: $nombre")
    }
}

enum class MotivoAnulacion(val nombre: String, @StringRes val labelRes: Int) {
    REPACTACION("Repactación", R.string.cat_anulacion_repactacion),
    ERROR_REGISTRO("Error de registro", R.string.cat_anulacion_error_registro),
    PAGADO_DIRECTO("Pagado directamente a la empresa", R.string.cat_anulacion_pagado_directo),
    OTRO("Otro", R.string.cat_anulacion_otro);

    companion object {
        fun desdeNombre(nombre: String): MotivoAnulacion =
            entries.firstOrNull { it.nombre == nombre.trim() }
                ?: error("Motivo de anulación no reconocido: $nombre")
    }
}

// ---------- estado del compromiso (reutiliza el enum del home) ----------

val EstadoCompromiso.nombre: String
    get() = when (this) {
        EstadoCompromiso.PENDIENTE -> "Pendiente"
        EstadoCompromiso.VENCIDO -> "Vencido"
        EstadoCompromiso.PAGADO -> "Pagado"
        EstadoCompromiso.EN_REVISION -> "En revisión"
        EstadoCompromiso.ANULADO -> "Anulado"
    }

fun estadoCompromisoDesdeNombre(nombre: String): EstadoCompromiso =
    when (nombre.trim().lowercase()) {
        "pendiente" -> EstadoCompromiso.PENDIENTE
        "vencido" -> EstadoCompromiso.VENCIDO
        "pagado" -> EstadoCompromiso.PAGADO
        "en revisión", "en revision" -> EstadoCompromiso.EN_REVISION
        "anulado" -> EstadoCompromiso.ANULADO
        else -> error("Estado de compromiso no reconocido: $nombre")
    }
