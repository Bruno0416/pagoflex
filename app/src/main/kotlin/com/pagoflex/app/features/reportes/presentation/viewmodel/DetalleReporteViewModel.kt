package com.pagoflex.app.features.reportes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.presentation.model.DetalleReporteUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class DetalleReporteViewModel(
    private val reportesRepository: ReportesRepository,
    private val ahora: () -> LocalDateTime = LocalDateTime::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleReporteUiState())
    val uiState: StateFlow<DetalleReporteUiState> = _uiState.asStateFlow()

    fun cargar(numero: String) {
        viewModelScope.launch {
            try {
                val reporte = reportesRepository.obtenerDetalle(numero, ahora().toLocalDate())
                _uiState.update { it.copy(cargando = false, reporte = reporte) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, error = R.string.rep_error_cargar) }
            }
        }
    }

    fun setRespuesta(respuesta: String) {
        _uiState.update { it.copy(respuesta = respuesta, error = null) }
    }

    // queda como responsable el agente que lo toma (RN-23)
    fun tomar(agenteId: Int) {
        val numero = uiState.value.reporte?.numero ?: return
        ejecutar(numero, R.string.rep_tomado_ok) {
            reportesRepository.tomar(numero, agenteId, ahora())
        }
    }

    // sin respuesta para el usuario no se puede resolver (HU-10 CA-3)
    fun resolver() {
        val estado = uiState.value
        val numero = estado.reporte?.numero ?: return
        ejecutar(numero, R.string.rep_resuelto_ok) {
            reportesRepository.resolver(numero, estado.respuesta, ahora())
        }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    // ejecutar una acción del agente, mostrar su mensaje y recargar el detalle
    private fun ejecutar(numero: String, mensajeOk: Int, accion: suspend () -> Int?) {
        // 1. cancelar si ya se está guardando
        if (uiState.value.guardando) return
        _uiState.update { it.copy(guardando = true, error = null) }

        viewModelScope.launch {
            try {
                // 2. ejecutar y mostrar error o mensaje
                val error = accion()
                if (error != null) {
                    _uiState.update { it.copy(error = error) }
                } else {
                    _uiState.update { it.copy(mensaje = mensajeOk, respuesta = "") }
                    cargar(numero)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(error = R.string.rep_error_generico) }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }
}
