package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.Compromiso
import com.pagoflex.app.features.cobranza.data.CompromisoEmpresaRow
import com.pagoflex.app.features.detalle.data.DetalleCompromisoRow
import com.pagoflex.app.features.home.data.CompromisoHomeRow
import com.pagoflex.app.features.reconocimientos.data.CompromisoPagoRow
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

    // ---------- adonys: cobranza del ejecutivo (HU-11, HU-12) ----------

    // solo compromisos de la empresa del ejecutivo (RN-22)
    @Query("$SELECT_EMPRESA WHERE c.id_empresa = :empresaId ORDER BY c.fecha_vencimiento ASC, c.folio ASC")
    suspend fun obtenerDeEmpresa(empresaId: Int): List<CompromisoEmpresaRow>

    @Query("$SELECT_EMPRESA WHERE c.id_empresa = :empresaId AND c.folio = :folio LIMIT 1")
    suspend fun obtenerDeEmpresaPorFolio(empresaId: Int, folio: String): CompromisoEmpresaRow?

    @Query("SELECT * FROM compromiso WHERE folio = :folio LIMIT 1")
    suspend fun obtenerPorFolio(folio: String): Compromiso?

    @Query("SELECT folio FROM compromiso WHERE folio LIKE :prefijo || '%'")
    suspend fun obtenerFolios(prefijo: String): List<String>

    // misma persona, empresa, concepto y cuota (RN-20)
    @Query(
        """
        SELECT COUNT(*) FROM compromiso
        WHERE id_usuario_final = :usuarioId
            AND id_empresa = :empresaId
            AND LOWER(TRIM(concepto)) = LOWER(TRIM(:concepto))
            AND (numero_cuota = :numeroCuota OR (numero_cuota IS NULL AND :numeroCuota IS NULL))
    """
    )
    suspend fun contarDuplicados(
        usuarioId: Int,
        empresaId: Int,
        concepto: String,
        numeroCuota: Int?
    ): Int

    @Query(
        """
        UPDATE compromiso
        SET id_estado_compromiso = :idEstado,
            recargo_aplicado = :recargoAplicado,
            fecha_cambio_estado = :fechaCambio
        WHERE id_compromiso = :idCompromiso
    """
    )
    suspend fun actualizarEstado(
        idCompromiso: Int,
        idEstado: Int,
        recargoAplicado: Int,
        fechaCambio: LocalDateTime
    ): Int

    companion object {
        // compromiso con persona, pago, registro y anulación
        const val SELECT_EMPRESA = """
            SELECT
                c.id_compromiso AS idCompromiso,
                c.folio AS folio,
                c.id_empresa AS idEmpresa,
                c.concepto AS concepto,
                c.numero_cuota AS numeroCuota,
                c.total_cuotas AS totalCuotas,
                c.monto AS monto,
                c.recargo_aplicado AS recargoAplicado,
                e.recargo_atraso AS recargoEmpresa,
                c.fecha_vencimiento AS fechaVencimiento,
                ec.nombre AS estado,
                u.id_usuario AS idUsuarioFinal,
                u.nombres AS nombres,
                u.apellidos AS apellidos,
                u.rut AS rut,
                u.correo AS correo,
                u.telefono AS telefono,
                p.numero_comprobante AS numeroComprobante,
                p.fecha_hora AS fechaPago,
                p.monto_pagado AS montoPagado,
                ca.nombre AS canal,
                c.fecha_registro AS fechaRegistro,
                ej.nombres || ' ' || ej.apellidos AS registradoPor,
                ma.nombre AS motivoAnulacion,
                a.comentario AS comentarioAnulacion,
                a.fecha AS fechaAnulacion,
                ea.nombres || ' ' || ea.apellidos AS anuladoPor
            FROM compromiso AS c
            INNER JOIN empresa_cliente AS e ON e.id_empresa = c.id_empresa
            INNER JOIN estado_compromiso AS ec ON ec.id_estado_compromiso = c.id_estado_compromiso
            INNER JOIN usuario AS u ON u.id_usuario = c.id_usuario_final
            LEFT JOIN pago AS p ON p.id_compromiso = c.id_compromiso
            LEFT JOIN canal AS ca ON ca.id_canal = p.id_canal
            LEFT JOIN usuario AS ej ON ej.id_usuario = c.id_ejecutivo_registro
            LEFT JOIN anulacion AS a ON a.id_compromiso = c.id_compromiso
            LEFT JOIN motivo_anulacion AS ma ON ma.id_motivo_anulacion = a.id_motivo_anulacion
            LEFT JOIN usuario AS ea ON ea.id_usuario = a.id_ejecutivo
        """
    }
    // -------------- detalle compromiso --------------
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
