package com.pagoflex.app.features.detalle.presentation.viewmodel

import android.util.Log
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
import retrofit2.HttpException

class DetalleViewModel(private val repository: DetalleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarDetalle(folio: String) {
        // 1. rechazar folios vacios antes de consultar
        if (folio.isBlank()) {
            _uiState.update {
                it.copy(cargando = false, detalle = null, mensajeError = R.string.detalle_no_encontrado)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null, detalle = null) }
            try {
                // 2. consultar el compromiso por folio
                val detalle = withContext(Dispatchers.IO) { repository.obtenerDetalle(folio) }
                // 3. publicar el detalle obtenido
                _uiState.update {
                    it.copy(
                        cargando = false,
                        detalle = detalle,
                        mensajeError = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                // 3. diferenciar un folio inexistente de un fallo general
                Log.e("PagoFlexDetalle", "Error HTTP al consultar el folio", e)
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError = if (e.code() == 404) R.string.detalle_no_encontrado
                                       else R.string.detalle_error_cargar
                    )
                }
            } catch (e: Exception) {
                Log.e("PagoFlexDetalle", "Error al consultar el folio", e)
                _uiState.update { it.copy(cargando = false, mensajeError = R.string.detalle_error_cargar) }
            }
        }
    }
}
