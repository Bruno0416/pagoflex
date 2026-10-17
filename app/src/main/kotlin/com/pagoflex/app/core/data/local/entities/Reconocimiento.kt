package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

// catalogo de reconocimientos simbolicos por hechos verificables
@Entity(
    tableName = "reconocimiento",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class Reconocimiento(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_reconocimiento")
    val idReconocimiento: Int,

    val nombre: String,

    val descripcion: String
)
