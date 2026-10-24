package com.pagoflex.app.core.data.local.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "pregunta_recuperacion",
    indices = [Index(value = ["pregunta"], unique = true)]
)
data class PreguntaRecuperacion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_pregunta_recuperacion")
    val idPreguntaRecuperacion: Int = 0,
    val pregunta: String
)
