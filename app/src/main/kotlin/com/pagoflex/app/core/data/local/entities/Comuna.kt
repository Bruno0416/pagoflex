package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

// comunas usadas por usuarios finales y recomendaciones
@Entity(
    tableName = "comuna",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class Comuna(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_comuna")
    val idComuna: Int = 0,

    val nombre: String
)
