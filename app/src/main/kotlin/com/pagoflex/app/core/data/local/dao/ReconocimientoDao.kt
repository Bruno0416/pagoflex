package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.pagoflex.app.features.reconocimientos.data.ReconocimientoObtenidoRow
import com.pagoflex.app.core.data.local.entities.Reconocimiento
import com.pagoflex.app.core.data.local.entities.UsuarioReconocimiento

@Dao
interface ReconocimientoDao {
    @Insert
    suspend fun insertarReconocimiento(reconocimiento: Reconocimiento): Long

    @Insert
    suspend fun insertarUsuarioReconocimiento(usuarioReconocimiento: UsuarioReconocimiento): Long

    // ---------- catalogo y otorgamientos ----------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarCatalogoSiNoExiste(reconocimiento: Reconocimiento): Long

    @Query("SELECT id_reconocimiento FROM reconocimiento WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerIdPorNombre(nombre: String): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun darSiNoExiste(reconocimiento: UsuarioReconocimiento): Long

    // ---------- reconocimientos del usuario ----------
    @Query(
        """
        SELECT r.nombre AS nombre, r.descripcion AS descripcion,
               ur.fecha_obtencion AS fechaObtencion, ur.motivo AS motivo
        FROM usuario_reconocimiento AS ur
        INNER JOIN reconocimiento AS r
            ON r.id_reconocimiento = ur.id_reconocimiento
        WHERE ur.id_usuario_final = :usuarioId
        ORDER BY ur.fecha_obtencion DESC, r.nombre ASC
        """
    )
    suspend fun obtenerDelUsuario(usuarioId: Int): List<ReconocimientoObtenidoRow>
}
