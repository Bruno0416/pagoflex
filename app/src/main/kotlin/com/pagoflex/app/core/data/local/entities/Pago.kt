package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDateTime

// pago confirmado de un compromiso, que equivale al comprobante
@Entity(
    tableName = "pago",
    foreignKeys = [
        ForeignKey(
            entity = Compromiso::class,
            parentColumns = ["id_compromiso"],
            childColumns = ["id_compromiso"]
        ),
        ForeignKey(
            entity = Canal::class,
            parentColumns = ["id_canal"],
            childColumns = ["id_canal"]
        ),
        ForeignKey(
            entity = MedioPago::class,
            parentColumns = ["id_medio_pago"],
            childColumns = ["id_medio_pago"]
        )
    ],
    indices = [
        Index(value = ["numero_comprobante"], unique = true),
        Index(value = ["id_compromiso"], unique = true),
        Index(value = ["id_canal"]),
        Index(value = ["id_medio_pago"])
    ]
)
data class Pago(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_pago")
    val idPago: Int,

    @ColumnInfo(name = "numero_comprobante")
    val numeroComprobante: String,

    // cada compromiso admite como maximo un pago
    @ColumnInfo(name = "id_compromiso")
    val idCompromiso: Int,

    @ColumnInfo(name = "fecha_hora")
    val fechaHora: LocalDateTime,

    // monto total pagado, incluido el recargo cuando corresponde
    @ColumnInfo(name = "monto_pagado")
    val montoPagado: Int,

    @ColumnInfo(name = "id_canal")
    val idCanal: Int,

    @ColumnInfo(name = "id_medio_pago")
    val idMedioPago: Int
) {
    init {
        require(montoPagado > 0)
    }
}
