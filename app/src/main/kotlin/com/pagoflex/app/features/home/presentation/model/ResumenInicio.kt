package com.pagoflex.app.features.home.presentation.model

import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.home.data.remote.dto.response.CumplimientoHomeResponse

data class ResumenInicio(
    val totalPorPagar: Long = 0L,
    val totalRecargos: Long = 0L,
    val cantidadCompromisos: Int = 0,
    val cantidadOrganizaciones: Int = 0,
    val cantidadVencidos: Int = 0,
    val cumplimiento: CumplimientoHomeResponse = CumplimientoHomeResponse(0, 0, null, null),
    val proximoCompromiso: MiCompromisoResponse? = null,
    val vencidoDestacado: MiCompromisoResponse? = null
)
