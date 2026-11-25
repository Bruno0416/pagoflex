package com.pagoflex.app.features.historial.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.historial.data.HistorialRepository
import com.pagoflex.app.features.historial.presentation.model.ComprobanteUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ComprobanteViewModel(private val repository: HistorialRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ComprobanteUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarComprobante(numeroComprobante: String) {
        viewModelScope.launch {
            _uiState.value = ComprobanteUiState(cargando = true)
            try {
                // 1. solicitar el comprobante por su numero
                val comprobante = repository.obtenerComprobante(numeroComprobante)
                // 2. publicar el comprobante obtenido
                _uiState.value = ComprobanteUiState(comprobante = comprobante, cargando = false)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // 2. mostrar un error si la consulta falla
                _uiState.value = ComprobanteUiState(
                    cargando = false,
                    mensajeError = R.string.comprobante_error_cargar
                )
            }
        }
    }
}
