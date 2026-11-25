package com.pagoflex.app.features.home.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.R

enum class EstadoCompromiso(@StringRes val labelRes: Int) {
    PENDIENTE(R.string.home_estado_pendiente),
    VENCIDO(R.string.home_estado_vencido),
    PAGADO(R.string.home_estado_pagado),
    EN_REVISION(R.string.home_estado_en_revision),
    ANULADO(R.string.home_estado_anulado)
}
