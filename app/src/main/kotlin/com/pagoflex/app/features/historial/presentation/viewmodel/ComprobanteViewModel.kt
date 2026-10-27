package com.pagoflex.app.features.historial.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.historial.data.HistorialRepository
import com.pagoflex.app.features.historial.presentation.model.ComprobanteUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComprobanteViewModel(private val repository: HistorialRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ComprobanteUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarComprobante(numeroComprobante: String, usuarioId: Int) {
        viewModelScope.launch {
            try {
                val comprobante = withContext(Dispatchers.IO) {
                    repository.obtenerDetalleCompromiso(numeroComprobante, usuarioId)
                }

                _uiState.update {
                    it.copy(
                        comprobante = comprobante,
                        cargando = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError = R.string.comprobante_error_cargar
                    )
                }
            }
        }
    }

}
