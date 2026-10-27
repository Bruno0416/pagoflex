package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

// motivos permitidos al reportar un problema con un compromiso
@Entity(
    tableName = "motivo_reporte",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class MotivoReporte(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_motivo_reporte")
    val idMotivoReporte: Int = 0,

    val nombre: String
)
