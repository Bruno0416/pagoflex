package com.pagoflex.app.features.historial.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.historial.data.HistorialRepository
import com.pagoflex.app.features.historial.presentation.model.HistorialUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialViewModel(
    private val repositorio: HistorialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarHistorial(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val comprobantes = withContext(Dispatchers.IO) {
                    repositorio.obtenerHistorial(usuarioId)
                }

                _uiState.value = HistorialUiState(
                    comprobantes = comprobantes,
                    cargando = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value = HistorialUiState(
                    cargando = false,
                    mensajeError = R.string.historial_error_cargar
                )
            }
        }
    }
}
