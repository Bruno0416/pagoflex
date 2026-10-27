package com.pagoflex.app.features.detalle.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.features.detalle.data.DetalleCompromisoRow

data class DetalleUiState(
    val cargando: Boolean = true,
    val detalle: DetalleCompromisoRow? = null,
    val estadoActual: String = "",
    val diasAtraso: Long = 0L,
    val recargo: Int = 0,
    val total: Long = 0L,
    @StringRes val mensajeError: Int? = null
) {
    val puedeIniciarPago: Boolean
        get() = detalle != null && estadoActual.lowercase() in listOf(
            "pendiente",
            "vencido"
        )
}
