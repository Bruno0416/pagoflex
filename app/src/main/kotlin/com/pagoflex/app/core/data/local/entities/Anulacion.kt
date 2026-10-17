package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDateTime

// registro de la anulacion de un compromiso, con un maximo de una por compromiso
@Entity(
    tableName = "anulacion",
    foreignKeys = [
        ForeignKey(
            entity = Compromiso::class,
            parentColumns = ["id_compromiso"],
            childColumns = ["id_compromiso"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_ejecutivo"]
        ),
        ForeignKey(
            entity = MotivoAnulacion::class,
            parentColumns = ["id_motivo_anulacion"],
            childColumns = ["id_motivo_anulacion"]
        )
    ],
    indices = [
        Index(value = ["id_ejecutivo"]),
        Index(value = ["id_motivo_anulacion"])
    ]
)
data class Anulacion(
    @PrimaryKey
    @ColumnInfo(name = "id_compromiso")
    val idCompromiso: Int,

    // ejecutivo de la misma empresa que el compromiso
    @ColumnInfo(name = "id_ejecutivo")
    val idEjecutivo: Int,

    @ColumnInfo(name = "id_motivo_anulacion")
    val idMotivoAnulacion: Int,

    // obligatorio cuando el motivo es otro
    val comentario: String? = null,

    val fecha: LocalDateTime
)
