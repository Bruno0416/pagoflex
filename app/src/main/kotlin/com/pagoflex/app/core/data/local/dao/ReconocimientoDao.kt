package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import com.pagoflex.app.core.data.local.entities.Reconocimiento
import com.pagoflex.app.core.data.local.entities.UsuarioReconocimiento

@Dao
interface ReconocimientoDao {
    @Insert
    suspend fun insertarReconocimiento(reconocimiento: Reconocimiento): Long

    @Insert
    suspend fun insertarUsuarioReconocimiento(usuarioReconocimiento: UsuarioReconocimiento): Long
}
