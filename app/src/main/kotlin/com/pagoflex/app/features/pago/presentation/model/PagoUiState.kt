package com.pagoflex.app.features.pago.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.features.pago.data.MetodoPagoModel
import com.pagoflex.app.features.pago.data.remote.dto.response.CotizacionPagoResponse

enum class PasoPago { METODO, CONFIRMACION }

data class PagoUiState(
    val cargando: Boolean = true,
    val procesando: Boolean = false,
    val paso: PasoPago = PasoPago.METODO,
    val resumen: CotizacionPagoResponse? = null,
    val metodo: MetodoPagoModel? = null,
    @StringRes val error: Int? = null,
    val folioBloqueante: String? = null,
    val comprobanteConfirmado: String? = null,
    val resultadoIncierto: Boolean = false
)
