package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey


@Entity(
    tableName = "medio_pago",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class MedioPago(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_medio_pago")
    val idMedioPago: Int = 0,
    val nombre: String
)
