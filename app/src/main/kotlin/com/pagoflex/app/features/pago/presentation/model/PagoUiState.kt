package com.pagoflex.app.features.pago.presentation.model

import androidx.annotation.StringRes
import com.pagoflex.app.features.pago.data.MetodoPago
import com.pagoflex.app.features.pago.data.ResumenPago

enum class PasoPago { METODO, DATOS, CONFIRMACION }

data class PagoUiState(
    val cargando: Boolean = true,
    val procesando: Boolean = false,
    val paso: PasoPago = PasoPago.METODO,
    val resumen: ResumenPago? = null,
    val metodo: MetodoPago? = null,
    val identificadorGuardado: String? = null,
    val usarGuardado: Boolean = false,
    val numeroTarjeta: String = "",
    val vencimientoTarjeta: String = "",
    val codigoSeguridad: String = "",
    val recordar: Boolean = false,

    @StringRes val errorNumeroTarjeta: Int? = null,
    @StringRes val errorVencimientoTarjeta: Int? = null,
    @StringRes val errorCodigoSeguridad: Int? = null,
    @StringRes val error: Int? = null,
    val folioBloqueante: String? = null,
    val comprobanteConfirmado: String? = null
)
