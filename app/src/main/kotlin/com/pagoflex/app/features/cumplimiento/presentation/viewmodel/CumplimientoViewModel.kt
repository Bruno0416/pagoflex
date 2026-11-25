package com.pagoflex.app.features.cumplimiento.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.cumplimiento.data.CumplimientoRepository
import com.pagoflex.app.features.cumplimiento.presentation.model.CumplimientoUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CumplimientoViewModel(
    private val repositorio: CumplimientoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CumplimientoUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarCumplimiento() {
        viewModelScope.launch {
            // 1. consultar el indicador calculado por el servidor
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            try {
                val resultado = repositorio.obtenerCumplimiento()
                // 2. mostrar la categoria y el porcentaje recibidos
                _uiState.update { it.copy(resultado = resultado, cargando = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // 3. informar el fallo sin inventar un porcentaje
                _uiState.update { it.copy(cargando = false, mensajeError = R.string.cumplimiento_error_cargar) }
            }
        }
    }
}
