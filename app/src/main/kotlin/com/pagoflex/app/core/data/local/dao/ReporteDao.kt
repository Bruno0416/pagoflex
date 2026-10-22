package com.pagoflex.app.core.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.pagoflex.app.core.data.local.entities.Reporte
import com.pagoflex.app.features.reportes.data.ReporteRow
import java.time.LocalDateTime

@Dao
interface ReporteDao {
    @Insert
    suspend fun insertarReporte(reporte: Reporte): Long

    // ---------- reportes de problema (HU-06, HU-10) ----------

    // el agente ve los reportes de todos los usuarios (RF-14)
    @Query("$SELECT_REPORTE ORDER BY r.fecha_creacion ASC")
    suspend fun obtenerTodos(): List<ReporteRow>

    @Query("$SELECT_REPORTE WHERE r.numero = :numero LIMIT 1")
    suspend fun obtenerPorNumero(numero: String): ReporteRow?

    // el usuario solo ve sus propios reportes
    @Query("$SELECT_REPORTE WHERE c.id_usuario_final = :usuarioId ORDER BY r.fecha_creacion DESC")
    suspend fun obtenerDeUsuario(usuarioId: Int): List<ReporteRow>

    // un compromiso solo puede tener un reporte sin resolver (RN-09)
    @Query("$SELECT_REPORTE WHERE c.folio = :folio AND est.nombre <> :estadoResuelto LIMIT 1")
    suspend fun obtenerActivoDeCompromiso(folio: String, estadoResuelto: String): ReporteRow?

    @Query("SELECT numero FROM reporte")
    suspend fun obtenerNumeros(): List<String>

    // queda registrado el agente responsable (RN-23)
    @Query(
        """
        UPDATE reporte
        SET id_estado_reporte = :idEstado, id_agente = :idAgente, fecha_toma = :fechaToma
        WHERE id_reporte = :idReporte
    """
    )
    suspend fun tomar(idReporte: Int, idEstado: Int, idAgente: Int, fechaToma: LocalDateTime): Int

    @Query(
        """
        UPDATE reporte
        SET id_estado_reporte = :idEstado, respuesta = :respuesta, fecha_resolucion = :fechaResolucion
        WHERE id_reporte = :idReporte
    """
    )
    suspend fun resolver(
        idReporte: Int,
        idEstado: Int,
        respuesta: String,
        fechaResolucion: LocalDateTime
    ): Int

    companion object {
        // reporte con su compromiso, empresa, usuario y agente
        const val SELECT_REPORTE = """
            SELECT
                r.id_reporte AS idReporte,
                r.numero AS numero,
                mr.nombre AS motivo,
                r.comentario AS comentario,
                r.fecha_creacion AS fechaCreacion,
                est.nombre AS estado,
                r.id_agente AS idAgente,
                ag.nombres || ' ' || ag.apellidos AS agente,
                r.fecha_toma AS fechaToma,
                r.respuesta AS respuesta,
                r.fecha_resolucion AS fechaResolucion,
                c.id_compromiso AS idCompromiso,
                c.folio AS folio,
                c.concepto AS concepto,
                c.numero_cuota AS numeroCuota,
                c.total_cuotas AS totalCuotas,
                c.monto AS monto,
                c.recargo_aplicado AS recargoAplicado,
                e.recargo_atraso AS recargoEmpresa,
                c.fecha_vencimiento AS fechaVencimiento,
                ec.nombre AS estadoCompromiso,
                e.id_empresa AS idEmpresa,
                e.nombre_fantasia AS empresa,
                u.id_usuario AS idUsuarioFinal,
                u.nombres || ' ' || u.apellidos AS usuario,
                u.correo AS correo,
                u.telefono AS telefono
            FROM reporte AS r
            INNER JOIN motivo_reporte AS mr ON mr.id_motivo_reporte = r.id_motivo_reporte
            INNER JOIN estado_reporte AS est ON est.id_estado_reporte = r.id_estado_reporte
            INNER JOIN compromiso AS c ON c.id_compromiso = r.id_compromiso
            INNER JOIN empresa_cliente AS e ON e.id_empresa = c.id_empresa
            INNER JOIN estado_compromiso AS ec ON ec.id_estado_compromiso = c.id_estado_compromiso
            INNER JOIN usuario AS u ON u.id_usuario = c.id_usuario_final
            LEFT JOIN usuario AS ag ON ag.id_usuario = r.id_agente
        """
    }
}
