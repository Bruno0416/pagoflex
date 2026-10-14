package com.pagoflex.app.features.home.viewmodel

import androidx.lifecycle.ViewModel
import com.pagoflex.app.features.home.model.CompromisoInicio
import com.pagoflex.app.features.home.model.EstadoCompromiso
import com.pagoflex.app.features.home.model.HomeUiState
import com.pagoflex.app.features.home.model.ResumenInicio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class HomeViewModel : ViewModel() {
    // estado privado
    private val _uiState = MutableStateFlow(HomeUiState())

    // estado público
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    // ----- cargar datos inicio -----
    fun cargarInicio(
        nombreUsuario: String,
        ultimaActualizacion: LocalDateTime,
        fechaReferencia: LocalDate,
        porcentajeCumplimiento: Double,
        compromisos: List<CompromisoInicio>,
    ) {
        // 1. obtener periodo actual
        val periodo = YearMonth.from(fechaReferencia)

        // 2. actualizar compromisos vencidos
        val compromisosActualizados = compromisos.map { c ->
            if (c.estado == EstadoCompromiso.PENDIENTE && c.fechaVencimiento.isBefore(
                    fechaReferencia
                )
            ) {
                c.copy(estado = EstadoCompromiso.VENCIDO)
            } else {
                c
            }
        }

        // 3. obtener compromisos por pagar
        val compromisosMes = compromisosActualizados.filter { c ->
            YearMonth.from(c.fechaVencimiento) == periodo && c.estado in listOf(
                EstadoCompromiso.PENDIENTE, EstadoCompromiso.VENCIDO
            )
        }.sortedBy { it.fechaVencimiento }

        // 4. separar pendientes y vencidos
        val pendientes = compromisosMes.filter { it.estado == EstadoCompromiso.PENDIENTE }
        val vencidos = compromisosMes.filter { it.estado == EstadoCompromiso.VENCIDO }

        // 5. calcular resumen
        val totalPorPagar = compromisosMes.sumOf { it.total }
        val totalRecargos = compromisosMes.sumOf { it.recargo }
        val cantidadCompromisos = compromisosMes.size
        val cantidadOrganizaciones = compromisosMes.map { it.organizacion }.distinct().size
        val cantidadVencidos = vencidos.size
        val proximoCompromiso = pendientes.firstOrNull()
        val vencidoDestacado = vencidos.firstOrNull()

        // 6. generar resumen mes
        val resumen = ResumenInicio(
            totalPorPagar = totalPorPagar,
            totalRecargos = totalRecargos,
            cantidadCompromisos = cantidadCompromisos,
            cantidadOrganizaciones = cantidadOrganizaciones,
            cantidadVencidos = cantidadVencidos,
            proximoCompromiso = proximoCompromiso,
            vencidoDestacado = vencidoDestacado,
            porcentajeCumplimiento = porcentajeCumplimiento,
        )

        // 7. actualizar estado pantalla
        _uiState.update { estado ->
            estado.copy(
                nombreUsuario = nombreUsuario,
                periodo = periodo,
                resumen = resumen,
                compromisos = compromisosMes,
                ultimaActualizacion = ultimaActualizacion,
                cargando = false,
                mensajeError = null
            )
        }
    }
}