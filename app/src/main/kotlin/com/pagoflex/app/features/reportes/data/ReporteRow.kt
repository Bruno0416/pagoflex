package com.pagoflex.app.features.reportes.data

import java.time.LocalDate
import java.time.LocalDateTime

// fila de un reporte con su compromiso, empresa, usuario y agente responsable
data class ReporteRow(
    val idReporte: Int,
    val numero: String,
    val motivo: String,
    val comentario: String?,
    val fechaCreacion: LocalDateTime,
    val estado: String,

    // agente responsable, nulo mientras está abierto
    val idAgente: Int?,
    val agente: String?,
    val fechaToma: LocalDateTime?,
    val respuesta: String?,
    val fechaResolucion: LocalDateTime?,

    // compromiso reportado
    val idCompromiso: Int,
    val folio: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Int,
    val recargoAplicado: Int,
    val recargoEmpresa: Int,
    val fechaVencimiento: LocalDate,
    val estadoCompromiso: String,
    val idEmpresa: Int,
    val empresa: String,

    // usuario se obtiene vía compromiso (mer)
    val idUsuarioFinal: Int,
    val usuario: String,
    val correo: String,
    val telefono: String?,
)
