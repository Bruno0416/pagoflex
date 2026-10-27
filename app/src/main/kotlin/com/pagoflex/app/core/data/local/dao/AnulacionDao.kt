package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.features.historial.data.AnulacionHistorialRow
import com.pagoflex.app.core.data.local.entities.Anulacion

@Dao
interface AnulacionDao {
    @Insert
    suspend fun insertarAnulacion(anulacion: Anulacion): Long

    // ---------- historial de anulaciones ----------
    @Query("""
        SELECT e.nombre_fantasia AS empresa, c.concepto,
               c.numero_cuota AS numeroCuota, c.total_cuotas AS totalCuotas,
               a.fecha
        FROM anulacion a
        INNER JOIN compromiso c ON c.id_compromiso = a.id_compromiso
        INNER JOIN empresa_cliente e ON e.id_empresa = c.id_empresa
        WHERE c.id_usuario_final = :usuarioId
        ORDER BY a.fecha DESC
    """)
    suspend fun obtenerHistorial(usuarioId: Int): List<AnulacionHistorialRow>
}
