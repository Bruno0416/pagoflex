package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

// estados: abierto, en gestion y resuelto
@Entity(
    tableName = "estado_reporte",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class EstadoReporte(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_estado_reporte")
    val id: Int = 0,

    val nombre: String
)
