package com.pagoflex.app.features.home.presentation.model

data class ResumenInicio(
    val totalPorPagar: Long = 0L,
    val totalRecargos: Long = 0L,
    val cantidadCompromisos: Int = 0,
    val cantidadOrganizaciones: Int = 0,
    val cantidadVencidos: Int = 0,
    val porcentajeCumplimiento: Double? = null,
    val proximoCompromiso: CompromisoInicio? = null,
    val vencidoDestacado: CompromisoInicio? = null
)
