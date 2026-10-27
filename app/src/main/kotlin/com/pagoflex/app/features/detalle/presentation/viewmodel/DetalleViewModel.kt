package com.pagoflex.app.features.detalle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.detalle.data.DetalleRepository
import com.pagoflex.app.features.detalle.presentation.model.DetalleUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DetalleViewModel(private val repository: DetalleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState = _uiState.asStateFlow()


    fun cargarDetalle(folio: String, usuarioId: Int) {
        if (folio.isBlank() || usuarioId <= 0) {
            _uiState.update { it.copy(cargando = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }

            try {
                // 1. obtener compromiso
                val detalle = withContext(Dispatchers.IO) {
                    repository.obtenerDetalle(folio, usuarioId)
                }

                if (detalle == null) {
                    _uiState.update {
                        it.copy(cargando = false)
                    }

                    return@launch
                }

                // 2. calcular información
                val fechaActual = LocalDate.now()

                val estado = repository.obtenerEstadoActual(detalle, fechaActual)
                val diasAtraso = repository.calcularDiasAtraso(detalle, fechaActual)
                val recargo = repository.calcularRecargo(detalle, fechaActual)
                val total = repository.calcularTotal(detalle, fechaActual)

                // 3. actualizar estado
                _uiState.update {
                    it.copy(
                        cargando = false,
                        detalle = detalle,
                        estadoActual = estado,
                        diasAtraso = diasAtraso,
                        recargo = recargo,
                        total = total
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError = R.string.detalle_error_cargar
                    )
                }
            }

        }
    }
}
