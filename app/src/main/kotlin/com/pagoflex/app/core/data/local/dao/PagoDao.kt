package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import com.pagoflex.app.core.data.local.entities.Pago

@Dao
interface PagoDao {
    @Insert
    suspend fun insertarPago(pago: Pago): Long
}
