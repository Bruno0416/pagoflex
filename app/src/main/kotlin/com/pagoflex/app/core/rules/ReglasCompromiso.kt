package com.pagoflex.app.core.rules

import androidx.annotation.StringRes
import com.pagoflex.app.R
import com.pagoflex.app.core.validators.Validaciones
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.LocalDate

// reglas de compromisos: vencimiento (RN-02), recargo (RN-03), registro (RN-20) y anulación (RN-21)
object ReglasCompromiso {

    private val estadosAnulables = setOf(
        EstadoCompromiso.PENDIENTE,
        EstadoCompromiso.VENCIDO,
        EstadoCompromiso.EN_REVISION,
    )

    // vence a las 23:59 del día, desde el día siguiente pasa a vencido
    fun estadoSegunFecha(fechaVencimiento: LocalDate, hoy: LocalDate): EstadoCompromiso =
        if (hoy.isAfter(fechaVencimiento)) EstadoCompromiso.VENCIDO else EstadoCompromiso.PENDIENTE

    // un pendiente guardado que ya venció se muestra como vencido (igual que en el home)
    fun estadoEfectivo(
        guardado: EstadoCompromiso,
        fechaVencimiento: LocalDate,
        hoy: LocalDate,
    ): EstadoCompromiso =
        if (guardado == EstadoCompromiso.PENDIENTE) estadoSegunFecha(fechaVencimiento, hoy) else guardado

    // el recargo fijo se suma una sola vez cuando el compromiso queda vencido
    fun recargoEfectivo(
        estado: EstadoCompromiso,
        recargoAplicado: Int,
        recargoEmpresa: Int,
    ): Int =
        if (estado == EstadoCompromiso.VENCIDO && recargoAplicado == 0) recargoEmpresa else recargoAplicado

    fun diasDeAtraso(fechaVencimiento: LocalDate, hoy: LocalDate): Long =
        maxOf(0L, hoy.toEpochDay() - fechaVencimiento.toEpochDay())

    fun puedeAnularse(estado: EstadoCompromiso): Boolean = estado in estadosAnulables

    @StringRes
    fun validarAnulacion(
        estado: EstadoCompromiso,
        empresaCompromiso: Int,
        empresaEjecutivo: Int?,
        motivo: MotivoAnulacion?,
        comentario: String,
    ): Int? = when {
        // 1. solo compromisos de su propia empresa
        empresaCompromiso != empresaEjecutivo -> R.string.cob_error_otra_empresa

        // 2. un compromiso pagado nunca se anula
        estado == EstadoCompromiso.PAGADO -> R.string.cob_error_anular_pagado

        // 3. anulado no cambia más de estado
        !puedeAnularse(estado) -> R.string.cob_error_ya_anulado

        // 4. motivo obligatorio y comentario si es otro
        motivo == null -> R.string.cob_error_motivo_obligatorio

        else -> Validaciones.validarComentario(motivo == MotivoAnulacion.OTRO, comentario)
    }

    // CP-2026-0112 -> CP-2026-0113
    fun siguienteFolio(foliosExistentes: List<String>, anio: Int): String {
        val prefijo = "CP-$anio-"
        val ultimo = foliosExistentes
            .filter { it.startsWith(prefijo) }
            .mapNotNull { it.removePrefix(prefijo).toIntOrNull() }
            .maxOrNull() ?: 0

        return prefijo + (ultimo + 1).toString().padStart(4, '0')
    }
}
