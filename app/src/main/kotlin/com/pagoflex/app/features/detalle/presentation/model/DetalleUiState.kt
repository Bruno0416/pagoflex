package com.pagoflex.app.features.detalle.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse

data class DetalleUiState(
    val cargando: Boolean = true,
    val detalle: MiCompromisoResponse? = null,
    @StringRes val mensajeError: Int? = null
) {
    val puedeIniciarPago: Boolean
        get() = detalle?.pagable == true
}
