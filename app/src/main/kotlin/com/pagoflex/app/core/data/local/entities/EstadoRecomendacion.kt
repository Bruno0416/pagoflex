package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey


@Entity(
    tableName = "estado_recomendacion",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class EstadoRecomendacion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_estado_recomendacion")
    val idEstadoRecomendacion: Int = 0,

    val nombre: String,

    // secuencia utilizada para evitar saltos de estados
    val orden: Int
) {
    init {
        require(orden >= 0)
    }
}
