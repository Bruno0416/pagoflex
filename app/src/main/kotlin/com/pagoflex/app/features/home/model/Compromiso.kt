package com.pagoflex.app.features.home.model

import java.time.LocalDate

enum class EstadoCompromiso {
    PENDIENTE,
    VENCIDO,
    PAGADO,
    EN_REVISION,
    ANULADO
}

data class CompromisoInicio(
    val id: String,
    val empresa: String,
    val concepto: String,
    val numeroCuota: Int? = null,
    val totalCuota: Int? = null,
    val fechaVencimiento: LocalDate,
    val monto: Long,
    val recargo: Long = 0,
    val estado: EstadoCompromiso,
) {
    val total get() = monto + recargo
}