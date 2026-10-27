package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.Compromiso
import com.pagoflex.app.features.reconocimientos.data.CompromisoPagoRow
import com.pagoflex.app.features.detalle.data.DetalleCompromisoRow
import com.pagoflex.app.features.home.data.CompromisoHomeRow
import java.time.LocalDate
import java.time.LocalDateTime

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

    // ---------- compromisos con pagos ----------
    @Query(
        """
        SELECT
            c.folio AS folio,
            c.fecha_vencimiento AS fechaVencimiento,
            ec.nombre AS estado,
            p.fecha_hora AS fechaPago
        FROM compromiso AS c
        INNER JOIN estado_compromiso AS ec
            ON ec.id_estado_compromiso = c.id_estado_compromiso
        LEFT JOIN pago AS p
            ON p.id_compromiso = c.id_compromiso
        WHERE c.id_usuario_final = :usuarioId
        ORDER BY c.fecha_vencimiento ASC, c.id_compromiso ASC
        """
    )
    suspend fun obtenerCompromisosConPago(usuarioId: Int): List<CompromisoPagoRow>

    // ---------- detalle de compromiso ----------
    @Query(
        """
        SELECT
            c.id_compromiso AS idCompromiso,
            c.id_empresa AS idEmpresa,
            c.folio AS folio,
            e.nombre_fantasia AS organizacion,
            c.concepto AS concepto,
            c.numero_cuota AS numeroCuota,
            c.total_cuotas AS totalCuotas,
            c.fecha_vencimiento AS fechaVencimiento,
            c.monto AS monto,
            c.recargo_aplicado AS recargoAplicado,
            e.recargo_atraso AS recargoEmpresa,
            ec.nombre AS estado,
            e.correo_atencion AS correoContacto
        FROM compromiso AS c
        INNER JOIN empresa_cliente AS e
            ON e.id_empresa = c.id_empresa
        INNER JOIN estado_compromiso AS ec
            ON ec.id_estado_compromiso = c.id_estado_compromiso
        WHERE c.folio = :folio
            AND c.id_usuario_final = :usuarioId
        LIMIT 1
        """
    )
    suspend fun obtenerDetalle(
        folio: String,
        usuarioId: Int
    ): DetalleCompromisoRow?
    // ---------- orden de pago ----------
    @Query(
        """
        SELECT c.folio FROM compromiso c
        INNER JOIN estado_compromiso ec
            ON ec.id_estado_compromiso = c.id_estado_compromiso
        WHERE c.id_usuario_final = :usuarioId
            AND c.id_empresa = :empresaId
            AND c.id_compromiso != :compromisoId
            AND c.fecha_vencimiento < :vencimiento
            AND ec.nombre IN ('Pendiente', 'Vencido')
        ORDER BY c.fecha_vencimiento ASC, c.id_compromiso ASC
        LIMIT 1
        """
    )
    suspend fun obtenerFolioAnteriorPagable(
        usuarioId: Int,
        empresaId: Int,
        vencimiento: LocalDate,
        compromisoId: Int
    ): String?

    // ---------- actualizacion del compromiso ----------
    @Query(
        """
        UPDATE compromiso SET
            id_estado_compromiso = :estadoPagadoId,
            recargo_aplicado = :recargo,
            fecha_cambio_estado = :fechaCambio
        WHERE id_compromiso = :compromisoId
          AND id_usuario_final = :usuarioId
          AND id_estado_compromiso IN (
              SELECT id_estado_compromiso FROM estado_compromiso
              WHERE nombre IN ('Pendiente', 'Vencido')
          )
        """
    )
    suspend fun marcarPagado(
        compromisoId: Int,
        usuarioId: Int,
        estadoPagadoId: Int,
        recargo: Int,
        fechaCambio: LocalDateTime
    ): Int
}
