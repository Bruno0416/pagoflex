package com.pagoflex.app.features.home.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.R
import java.time.LocalDate

enum class EstadoCompromiso(@StringRes val labelRes: Int) {
    PENDIENTE(R.string.home_estado_pendiente),
    VENCIDO(R.string.home_estado_vencido),
    PAGADO(R.string.home_estado_pagado),
    EN_REVISION(R.string.home_estado_en_revision),
    ANULADO(R.string.home_estado_anulado)
}

data class CompromisoInicio(
    val id: String,
    val organizacion: String,
    val siglaOrganizacion: String,
    val concepto: String,
    val numeroCuota: Int? = null,
    val totalCuotas: Int? = null,
    val fechaVencimiento: LocalDate,
    val monto: Long,
    val recargo: Long = 0L,
    val estado: EstadoCompromiso
) {
    val total: Long
        get() = monto + recargo
}
