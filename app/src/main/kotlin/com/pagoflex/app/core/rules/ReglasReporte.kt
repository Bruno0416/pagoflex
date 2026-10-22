package com.pagoflex.app.core.rules

import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.time.DayOfWeek
import java.time.LocalDate

// reglas de reportes de problema (RN-09) y su gestión por el agente (RN-23)
object ReglasReporte {

    private const val DIAS_HABILES_LIMITE = 2

    // solo se reporta lo que aún se debe; si está en revisión se muestra el reporte existente
    fun puedeReportarse(estado: EstadoCompromiso): Boolean =
        estado == EstadoCompromiso.PENDIENTE || estado == EstadoCompromiso.VENCIDO

    // días de lunes a viernes después de la fecha de inicio (sin feriados)
    fun diasHabilesEntre(desde: LocalDate, hasta: LocalDate): Int {
        var dias = 0
        var fecha = desde.plusDays(1)

        while (!fecha.isAfter(hasta)) {
            if (fecha.dayOfWeek != DayOfWeek.SATURDAY && fecha.dayOfWeek != DayOfWeek.SUNDAY) {
                dias++
            }
            fecha = fecha.plusDays(1)
        }

        return dias
    }

    // más de 2 días hábiles abierto se considera atrasado
    fun estaAtrasado(estado: EstadoReporte, fechaCreacion: LocalDate, hoy: LocalDate): Boolean =
        estado == EstadoReporte.ABIERTO &&
                diasHabilesEntre(fechaCreacion, hoy) > DIAS_HABILES_LIMITE

    fun puedeTomarse(estado: EstadoReporte): Boolean = estado == EstadoReporte.ABIERTO

    fun puedeResolverse(estado: EstadoReporte): Boolean = estado == EstadoReporte.EN_GESTION

    // al resolver queda anulado si el ejecutivo lo anuló, si no vuelve según la fecha
    fun estadoAlResolver(
        estadoActual: EstadoCompromiso,
        fechaVencimiento: LocalDate,
        hoy: LocalDate,
    ): EstadoCompromiso =
        if (estadoActual == EstadoCompromiso.ANULADO) {
            EstadoCompromiso.ANULADO
        } else {
            ReglasCompromiso.estadoSegunFecha(fechaVencimiento, hoy)
        }

    // REP-0012 -> REP-0013
    fun siguienteNumero(numerosExistentes: List<String>): String {
        val ultimo = numerosExistentes
            .mapNotNull { it.removePrefix("REP-").toIntOrNull() }
            .maxOrNull() ?: 0

        return "REP-" + (ultimo + 1).toString().padStart(4, '0')
    }
}
