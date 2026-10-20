package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.Compromiso
import com.pagoflex.app.features.home.data.CompromisoHomeRow

@Dao
interface CompromisoDao {
    @Insert
    suspend fun insertarCompromiso(compromiso: Compromiso): Long

    @Query(
        """
        SELECT
            c.folio AS folio,
            e.nombre_fantasia AS organizacion,
            c.concepto AS concepto,
            c.numero_cuota AS numeroCuota,
            c.total_cuotas AS totalCuotas,
            c.fecha_vencimiento AS fechaVencimiento,
            c.monto AS monto,
            c.recargo_aplicado AS recargoAplicado,
            e.recargo_atraso AS recargoEmpresa,
            ec.nombre AS estado
        FROM compromiso AS c
        INNER JOIN empresa_cliente AS e ON e.id_empresa = c.id_empresa
        INNER JOIN estado_compromiso AS ec
            ON ec.id_estado_compromiso = c.id_estado_compromiso
        WHERE c.id_usuario_final = :usuarioId
        ORDER BY c.fecha_vencimiento ASC, c.id_compromiso ASC
    """
    )
    suspend fun obtenerParaHome(usuarioId: Int): List<CompromisoHomeRow>
}
