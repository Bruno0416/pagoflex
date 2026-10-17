package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

// motivos de anulacion definidos por la empresa cliente
@Entity(
    tableName = "motivo_anulacion",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class MotivoAnulacion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_motivo_anulacion")
    val idMotivoAnulacion: Int = 0,

    val nombre: String
)
