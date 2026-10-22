package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.Usuario
import com.pagoflex.app.core.profiles.model.SesionUsuario

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Query("SELECT COUNT(*) FROM usuario")
    suspend fun contarUsuarios(): Int

    @Query(
        """
            SELECT
                u.id_usuario AS usuarioId,
                r.codigo AS perfil
            FROM usuario u
            INNER JOIN rol r ON u.id_rol = r.id_rol
            WHERE u.id_usuario = :usuarioId
                AND u.activo = 1
            LIMIT 1
        """
    )
    suspend fun obtenerSesionUsuario(usuarioId: Int): SesionUsuario?

    @Query(
        """
        SELECT nombres FROM usuario WHERE id_usuario = :usuarioId
    """
    )
    suspend fun obtenerNombreParaHome(usuarioId: Int): String?


    // ---------- cobranza y reportes ----------

    @Query("SELECT * FROM usuario WHERE id_usuario = :usuarioId LIMIT 1")
    suspend fun obtenerPorId(usuarioId: Int): Usuario?

    // el registro guarda el rut con puntos y guion: 15.834.207-3
    @Query("SELECT * FROM usuario WHERE rut = :rut LIMIT 1")
    suspend fun obtenerPorRut(rut: String): Usuario?
}
