package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import com.pagoflex.app.core.data.local.entities.Comprobante
import com.pagoflex.app.core.data.local.entities.Pago

@Dao
interface PagoDao {
    @Insert
    suspend fun insertarPagoInterno(pago: Pago): Long

    // ---------- generar comprobante ----------
    @Query(
        """
        INSERT INTO comprobante (
            numero_comprobante, id_pago, id_usuario_final,
            fecha_hora, nombre_pagador, rut,
            empresa, folio, concepto, numero_cuota,
            total_cuotas, fecha_vencimiento, monto_cuota,
            recargo, monto_pagado, medio_pago, canal
        )
        SELECT
            p.numero_comprobante, p.id_pago, c.id_usuario_final,
            p.fecha_hora, TRIM(u.nombres || ' ' || u.apellidos), u.rut,
            e.nombre_fantasia, c.folio, c.concepto, c.numero_cuota,
            c.total_cuotas, c.fecha_vencimiento, c.monto,
            p.monto_pagado - c.monto, p.monto_pagado, mp.nombre, ca.nombre
        FROM pago p
        INNER JOIN compromiso c ON c.id_compromiso = p.id_compromiso
        INNER JOIN usuario u ON u.id_usuario = c.id_usuario_final
        INNER JOIN empresa_cliente e ON e.id_empresa = c.id_empresa
        INNER JOIN medio_pago mp ON mp.id_medio_pago = p.id_medio_pago
        INNER JOIN canal ca ON ca.id_canal = p.id_canal
        WHERE p.id_pago = :idPago
        """
    )
    suspend fun generarComprobante(idPago: Long)

    // ---------- contar comprobantes ----------
    @Query("SELECT COUNT(*) FROM comprobante WHERE id_pago = :idPago")
    suspend fun contarComprobantes(idPago: Long): Int

    // ---------- procesar pago ----------
    @Transaction
    suspend fun procesarPago(pago: Pago): Long {
        val idPago = insertarPagoInterno(pago)

        generarComprobante(idPago)

        check(contarComprobantes(idPago) == 1) {
            "No se pudo generar el comprobante"
        }

        return idPago
    }

    // ---------- historial ----------
    @Query(
        """
        SELECT * FROM comprobante
        WHERE id_usuario_final = :usuarioId
        ORDER BY fecha_hora DESC
        """
    )
    suspend fun obtenerHistorial(usuarioId: Int): List<Comprobante>

    // ---------- detalle de comprobante ----------
    @Query(
        """
        SELECT * FROM comprobante
        WHERE numero_comprobante = :numeroComprobante
        AND id_usuario_final = :usuarioId
        LIMIT 1
        """
    )
    suspend fun obtenerDetalleComprobante(
        numeroComprobante: String,
        usuarioId: Int
    ): Comprobante?
}
