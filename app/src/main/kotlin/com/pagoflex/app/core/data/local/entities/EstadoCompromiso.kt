package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "estado_compromiso",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class EstadoCompromiso(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_estado_compromiso")
    val id: Int = 0,
    val nombre: String
)
