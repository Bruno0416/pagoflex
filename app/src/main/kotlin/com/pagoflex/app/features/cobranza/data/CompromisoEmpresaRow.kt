package com.pagoflex.app.features.cobranza.data

import java.time.LocalDate
import java.time.LocalDateTime

// fila de un compromiso de la empresa con su persona, pago, registro y anulación
data class CompromisoEmpresaRow(
    val idCompromiso: Int,
    val folio: String,
    val idEmpresa: Int,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Int,
    val recargoAplicado: Int,
    val recargoEmpresa: Int,
    val fechaVencimiento: LocalDate,
    val estado: String,

    // persona (solo nombre, rut y contacto, RN-22)
    val idUsuarioFinal: Int,
    val nombres: String,
    val apellidos: String,
    val rut: String,
    val correo: String,
    val telefono: String?,

    // pago, nulo si no está pagado
    val numeroComprobante: String?,
    val fechaPago: LocalDateTime?,
    val montoPagado: Int?,
    val canal: String?,

    // trazabilidad (RNF-15)
    val fechaRegistro: LocalDateTime,
    val registradoPor: String?,
    val motivoAnulacion: String?,
    val comentarioAnulacion: String?,
    val fechaAnulacion: LocalDateTime?,
    val anuladoPor: String?,
)
