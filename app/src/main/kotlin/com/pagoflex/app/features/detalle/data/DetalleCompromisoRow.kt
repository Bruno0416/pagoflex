package com.pagoflex.app.features.detalle.data

import java.time.LocalDate

data class DetalleCompromisoRow(
    val idCompromiso: Int,
    val idEmpresa: Int,
    val folio: String,
    val organizacion: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val fechaVencimiento: LocalDate,
    val monto: Int,
    val recargoAplicado: Int,
    val recargoEmpresa: Int,
    val estado: String,
    val correoContacto: String
)
