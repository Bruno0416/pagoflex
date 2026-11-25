package com.pagoflex.app.features.historial.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.historial.data.HistorialRepository
import com.pagoflex.app.features.historial.presentation.model.HistorialUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistorialViewModel(private val repositorio: HistorialRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarHistorial() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true, mensajeError = null)
            try {
                // 1. consultar pagos y anulaciones en paralelo
                val (comprobantes, anulaciones) = coroutineScope {
                    val pagos = async { repositorio.obtenerHistorial() }
                    val anulados = async { repositorio.obtenerAnulaciones() }
                    pagos.await() to anulados.await()
                }
                // 2. publicar los movimientos recibidos
                _uiState.value = HistorialUiState(
                    comprobantes = comprobantes,
                    anulaciones = anulaciones,
                    cargando = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // 2. informar si no se puede cargar el historial
                _uiState.value = HistorialUiState(
                    cargando = false,
                    mensajeError = R.string.historial_error_cargar
                )
            }
        }
    }
}
