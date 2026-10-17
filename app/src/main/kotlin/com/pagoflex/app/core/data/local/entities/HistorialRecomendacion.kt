package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDateTime

// cada cambio de estado realizado por un agente sobre una recomendacion
@Entity(
    tableName = "historial_recomendacion",
    foreignKeys = [
        ForeignKey(
            entity = Recomendacion::class,
            parentColumns = ["id_recomendacion"],
            childColumns = ["id_recomendacion"]
        ),
        ForeignKey(
            entity = EstadoRecomendacion::class,
            parentColumns = ["id_estado_recomendacion"],
            childColumns = ["id_estado_recomendacion"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_agente"]
        )
    ],
    indices = [
        Index(value = ["id_recomendacion"]),
        Index(value = ["id_estado_recomendacion"]),
        Index(value = ["id_agente"])
    ]
)
data class HistorialRecomendacion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_historial")
    val idHistorial: Int,

    @ColumnInfo(name = "id_recomendacion")
    val idRecomendacion: Int,

    // estado al que cambio la recomendacion
    @ColumnInfo(name = "id_estado_recomendacion")
    val idEstadoRecomendacion: Int,

    // agente que realizo el cambio
    @ColumnInfo(name = "id_agente")
    val idAgente: Int,

    val fecha: LocalDateTime,

    // obligatorio cuando la recomendacion se descarta
    val motivo: String? = null
)
