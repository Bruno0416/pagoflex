package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import com.pagoflex.app.core.data.local.entities.ContactoRecomendacion
import com.pagoflex.app.core.data.local.entities.HistorialRecomendacion
import com.pagoflex.app.core.data.local.entities.Recomendacion

@Dao
interface RecomendacionDao {
    @Insert
    suspend fun insertarRecomendacion(recomendacion: Recomendacion): Long

    @Insert
    suspend fun insertarContactoRecomendacion(contactoRecomendacion: ContactoRecomendacion): Long

    @Insert
    suspend fun insertarHistorialRecomendacion(historialRecomendacion: HistorialRecomendacion): Long
}
