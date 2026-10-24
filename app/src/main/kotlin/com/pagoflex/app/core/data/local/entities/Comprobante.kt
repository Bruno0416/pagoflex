package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "comprobante",
    indices = [
        Index(value = ["id_pago"], unique = true),
        Index(value = ["id_usuario_final"])
    ]
)
data class Comprobante(
    @PrimaryKey
    @ColumnInfo(name = "numero_comprobante")
    val numeroComprobante: String,
    @ColumnInfo(name = "id_pago")
    val idPago: Int,
    @ColumnInfo(name = "id_usuario_final")
    val idUsuarioFinal: Int,
    @ColumnInfo(name = "fecha_hora")
    val fechaHora: LocalDateTime,
    @ColumnInfo(name = "nombre_pagador")
    val nombrePagador: String,
    val rut: String,
    val empresa: String,
    val folio: String,
    val concepto: String,
    @ColumnInfo(name = "numero_cuota")
    val numeroCuota: Int?,
    @ColumnInfo(name = "total_cuotas")
    val totalCuotas: Int?,
    @ColumnInfo(name = "fecha_vencimiento")
    val fechaVencimiento: LocalDate,
    @ColumnInfo(name = "monto_cuota")
    val montoCuota: Int,
    val recargo: Int,
    @ColumnInfo(name = "monto_pagado")
    val montoPagado: Int,
    @ColumnInfo(name = "medio_pago")
    val medioPago: String,
    val canal: String
)