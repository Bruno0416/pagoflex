package com.pagoflex.app.features.home.data.remote.dto.response

data class ResumenHomeResponse(
    val mes: String,
    val porPagarMes: Long,
    val pagadoMes: Long,
    val pendientesMes: Int,
    val vencidos: Int,
    val montoVencido: Long,
    val proximos: List<MiCompromisoResponse>
)
