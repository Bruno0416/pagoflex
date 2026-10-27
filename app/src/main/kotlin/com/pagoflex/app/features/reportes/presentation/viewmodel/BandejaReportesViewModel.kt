package com.pagoflex.app.features.reportes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.presentation.model.BandejaReportesUiState
import com.pagoflex.app.features.reportes.presentation.model.ReporteItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class BandejaReportesViewModel(
    private val reportesRepository: ReportesRepository,
    private val hoy: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BandejaReportesUiState())
    val uiState: StateFlow<BandejaReportesUiState> = _uiState.asStateFlow()

    private var todos: List<ReporteItem> = emptyList()

    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            try {
                // 1. nombre, avatar del agente y reportes de todos los usuarios (RF-14)
                val cabecera = reportesRepository.obtenerCabecera(usuarioId)
                val iniciales = cabecera?.let { (it.nombres.trim().take(1) + it.apellidos.trim().take(1)).uppercase() }
                todos = reportesRepository.obtenerTodos(usuarioId, hoy())

                // 2. contar por estado y atrasados (RN-23)
                _uiState.update {
                    it.copy(
                        cargando = false,
                        nombreAgente = cabecera?.nombres?.substringBefore(" ").orEmpty(),
                        inicialesAgente = iniciales.orEmpty(),
                        avatarAgente = cabecera?.avatar,
                        cantidadPorEstado = todos.groupingBy { r -> r.estado }.eachCount(),
                        atrasados = todos.count { r -> r.atrasado },
                        error = null
                    )
                }
                aplicarFiltro()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, error = R.string.rep_error_cargar) }
            }
        }
    }

    fun filtrar(estado: EstadoReporte?) {
        _uiState.update { it.copy(filtro = estado) }
        aplicarFiltro()
    }

    // primero los atrasados y después los más antiguos (RNF-14)
    private fun aplicarFiltro() {
        val filtro = uiState.value.filtro
        val lista = todos
            .filter { filtro == null || it.estado == filtro }
            .sortedWith(compareByDescending<ReporteItem> { it.atrasado }.thenBy { it.fechaCreacion })

        _uiState.update { it.copy(reportes = lista) }
    }
}
