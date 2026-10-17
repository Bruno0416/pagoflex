package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import com.pagoflex.app.core.data.local.entities.Reporte

@Dao
interface ReporteDao {
    @Insert
    suspend fun insertarReporte(reporte: Reporte): Long
}
