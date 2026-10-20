package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.Credencial

@Dao
interface CredencialDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarCredencial(credencial: Credencial)

    @Query(
        """
        SELECT c.*
        FROM credencial c
        INNER JOIN usuario u ON c.id_usuario = u.id_usuario
        WHERE u.correo = :correo
        LIMIT 1
    """
    )
    suspend fun obtenerPorCorreo(correo: String): Credencial?
}