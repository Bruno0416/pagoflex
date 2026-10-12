package com.pagoflex.app.features.home.model

import java.time.LocalDate

data class ResumenInicio(
    val totalPendiente: Long = 0,
    val totalRecargos: Long = 0,
    val proximoVencimiento: LocalDate? = null,
    val empresaProximoVencimiento: String? = null,
    val cantidadVencidos: Int = 0
)