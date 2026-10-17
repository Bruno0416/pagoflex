package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import com.pagoflex.app.core.data.local.entities.Anulacion

@Dao
interface AnulacionDao {
    @Insert
    suspend fun insertarAnulacion(anulacion: Anulacion): Long
}
