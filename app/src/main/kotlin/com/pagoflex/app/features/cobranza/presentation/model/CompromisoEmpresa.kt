package com.pagoflex.app.features.cobranza.presentation.model

import com.pagoflex.app.core.rules.MotivoAnulacion
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.LocalDate
import java.time.LocalDateTime

// compromiso de la empresa como lo ve el ejecutivo
data class CompromisoEmpresa(
    val folio: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Long,
    val recargo: Long,
    val fechaVencimiento: LocalDate,
    val estado: EstadoCompromiso,
    val diasAtraso: Long,
    val persona: PersonaCompromiso,
    val pago: PagoCompromiso?,
    val registradoPor: String?,
    val fechaRegistro: LocalDateTime,
    val anulacion: AnulacionCompromiso?,
) {
    val total: Long
        get() = monto + recargo

    val textoCuota: String?
        get() = if (numeroCuota != null && totalCuotas != null) "$numeroCuota/$totalCuotas" else null
}

// de la persona solo nombre, rut y contacto (RN-22)
data class PersonaCompromiso(
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String?,
)

data class PagoCompromiso(
    val comprobante: String,
    val fecha: LocalDateTime,
    val canal: String,
    val monto: Long,
)

data class AnulacionCompromiso(
    val motivo: MotivoAnulacion,
    val comentario: String?,
    val fecha: LocalDateTime,
    val ejecutivo: String?,
)

// reporte del usuario que el ejecutivo ve en modo lectura (RF-17)
data class ReporteDelCompromiso(
    val numero: String,
    val motivoRes: Int,
    val comentario: String?,
    val estadoRes: Int,
    val fecha: LocalDateTime,
)
