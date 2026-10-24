package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.PreguntaRecuperacion

@Dao
interface PreguntaRecuperacionDao {
    @Insert
    suspend fun insertarPreguntas(preguntas: List<PreguntaRecuperacion>): List<Long>

    @Query("SELECT * FROM pregunta_recuperacion ORDER BY id_pregunta_recuperacion")
    suspend fun obtenerPreguntas(): List<PreguntaRecuperacion>
}
