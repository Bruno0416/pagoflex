package com.pagoflex.app.features.reportes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.presentation.model.MisReportesUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MisReportesViewModel(
    private val reportesRepository: ReportesRepository,
    private val hoy: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisReportesUiState())
    val uiState: StateFlow<MisReportesUiState> = _uiState.asStateFlow()

    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            try {
                // 1. reportes propios con su estado y respuesta (HU-06 CA-3)
                val reportes = reportesRepository.obtenerReportesDeUsuario(usuarioId, hoy())

                // 2. cobros vigentes que se pueden reportar
                val compromisos = reportesRepository.obtenerCompromisosReportables(usuarioId, hoy())

                _uiState.update {
                    it.copy(cargando = false, reportes = reportes, compromisos = compromisos, error = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, error = R.string.rep_error_cargar) }
            }
        }
    }
}
