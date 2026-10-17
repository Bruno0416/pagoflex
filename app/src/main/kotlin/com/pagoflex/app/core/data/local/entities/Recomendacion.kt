package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDateTime

// organizacion recomendada por un usuario final
@Entity(
    tableName = "recomendacion",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario_final"]
        ),
        ForeignKey(
            entity = Rubro::class,
            parentColumns = ["id_rubro"],
            childColumns = ["id_rubro"]
        ),
        ForeignKey(
            entity = Comuna::class,
            parentColumns = ["id_comuna"],
            childColumns = ["id_comuna"]
        ),
        ForeignKey(
            entity = EstadoRecomendacion::class,
            parentColumns = ["id_estado_recomendacion"],
            childColumns = ["id_estado_recomendacion"]
        )
    ],
    indices = [
        Index(value = ["numero"], unique = true),
        Index(value = ["id_usuario_final", "nombre_organizacion"], unique = true),
        Index(value = ["id_rubro"]),
        Index(value = ["id_comuna"]),
        Index(value = ["id_estado_recomendacion"])
    ]
)
data class Recomendacion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_recomendacion")
    val idRecomendacion: Int,

    val numero: String,

    @ColumnInfo(name = "id_usuario_final")
    val idUsuarioFinal: Int,

    @ColumnInfo(name = "nombre_organizacion")
    val nombreOrganizacion: String,

    @ColumnInfo(name = "rut_organizacion")
    val rutOrganizacion: String? = null,

    @ColumnInfo(name = "id_rubro")
    val idRubro: Int,

    @ColumnInfo(name = "id_comuna")
    val idComuna: Int,

    val fecha: LocalDateTime,

    // estado actual de la recomendacion
    @ColumnInfo(name = "id_estado_recomendacion")
    val idEstadoRecomendacion: Int
)
