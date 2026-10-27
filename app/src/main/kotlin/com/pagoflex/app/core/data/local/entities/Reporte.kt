package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDateTime

// problema informado por el usuario sobre uno de sus compromisos
@Entity(
    tableName = "reporte",
    foreignKeys = [
        ForeignKey(
            entity = Compromiso::class,
            parentColumns = ["id_compromiso"],
            childColumns = ["id_compromiso"]
        ),
        ForeignKey(
            entity = MotivoReporte::class,
            parentColumns = ["id_motivo_reporte"],
            childColumns = ["id_motivo_reporte"]
        ),
        ForeignKey(
            entity = EstadoReporte::class,
            parentColumns = ["id_estado_reporte"],
            childColumns = ["id_estado_reporte"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_agente"]
        )
    ],
    indices = [
        Index(value = ["numero"], unique = true),
        Index(value = ["id_compromiso"]),
        Index(value = ["id_motivo_reporte"]),
        Index(value = ["id_estado_reporte"]),
        Index(value = ["id_agente"])
    ]
)
data class Reporte(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_reporte")
    val idReporte: Int,

    val numero: String,

    // el usuario se obtiene desde el compromiso asociado
    @ColumnInfo(name = "id_compromiso")
    val idCompromiso: Int,

    @ColumnInfo(name = "id_motivo_reporte")
    val idMotivoReporte: Int,

    // obligatorio cuando el motivo es otro
    val comentario: String? = null,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: LocalDateTime,

    @ColumnInfo(name = "id_estado_reporte")
    val idEstadoReporte: Int,

    // nulo mientras el reporte permanece abierto
    @ColumnInfo(name = "id_agente")
    val idAgente: Int? = null,

    @ColumnInfo(name = "fecha_toma")
    val fechaToma: LocalDateTime? = null,

    // obligatoria para resolver un reporte
    val respuesta: String? = null,

    @ColumnInfo(name = "fecha_resolucion")
    val fechaResolucion: LocalDateTime? = null
)
