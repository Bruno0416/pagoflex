package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import java.time.LocalDateTime

// reconocimientos obtenidos por cada usuario final
@Entity(
    tableName = "usuario_reconocimiento",
    primaryKeys = ["id_usuario_final", "id_reconocimiento"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario_final"]
        ),
        ForeignKey(
            entity = Reconocimiento::class,
            parentColumns = ["id_reconocimiento"],
            childColumns = ["id_reconocimiento"]
        )
    ],
    indices = [
        Index(value = ["id_usuario_final"]),
        Index(value = ["id_reconocimiento"])
    ]
)
data class UsuarioReconocimiento(
    @ColumnInfo(name = "id_usuario_final")
    val idUsuarioFinal: Int,

    @ColumnInfo(name = "id_reconocimiento")
    val idReconocimiento: Int,

    @ColumnInfo(name = "fecha_obtencion")
    val fechaObtencion: LocalDateTime,

    // hecho verificable que dio origen al reconocimiento
    val motivo: String
)
