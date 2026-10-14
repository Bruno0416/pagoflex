package com.pagoflex.app.features.home.model

import java.time.LocalDate

enum class EstadoCompromiso(val label: String) {
    PENDIENTE("Pendiente"),
    VENCIDO("Vencido"),
    PAGADO("Pagado"),
    EN_REVISION("En revisión"),
    ANULADO("Anulado")
}

data class CompromisoInicio(
    val id: String,
    val organizacion: String,
    val siglaOrganizacion: String,
    val concepto: String,
    val numeroCuota: Int? = null,
    val totalCuotas: Int? = null,
    val fechaVencimiento: LocalDate,
    val monto: Long,
    val recargo: Long = 0L,
    val estado: EstadoCompromiso
) {
    val total: Long
        get() = monto + recargo
}
