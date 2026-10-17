package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "canal",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class Canal(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_canal")
    val idCanal: Int = 0,

    val nombre: String
)
