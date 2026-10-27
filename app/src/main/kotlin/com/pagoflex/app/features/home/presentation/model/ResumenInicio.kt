package com.pagoflex.app.features.home.presentation.model

import com.pagoflex.app.features.cumplimiento.data.ResultadoCumplimiento

data class ResumenInicio(
    val totalPorPagar: Long = 0L,
    val totalRecargos: Long = 0L,
    val cantidadCompromisos: Int = 0,
    val cantidadOrganizaciones: Int = 0,
    val cantidadVencidos: Int = 0,
    val cumplimiento: ResultadoCumplimiento = ResultadoCumplimiento(),
    val proximoCompromiso: CompromisoInicio? = null,
    val vencidoDestacado: CompromisoInicio? = null
)
