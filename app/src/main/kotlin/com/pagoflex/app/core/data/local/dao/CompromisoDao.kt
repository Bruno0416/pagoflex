package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import com.pagoflex.app.core.data.local.entities.Compromiso

@Dao
interface CompromisoDao {
    @Insert
    suspend fun insertarCompromiso(compromiso: Compromiso): Long
}
