package com.pagoflex.app.features.cumplimiento.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.cumplimiento.data.CumplimientoRepository
import com.pagoflex.app.features.cumplimiento.presentation.model.CumplimientoUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class CumplimientoViewModel(
    private val repositorio: CumplimientoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CumplimientoUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarCumplimiento(usuarioId: Int) {
        if (usuarioId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            try {
                // 1. obtener el resultado del periodo actual
                val hoy = LocalDate.now()
                val resultado = withContext(Dispatchers.IO) {
                    repositorio.obtenerCumplimiento(usuarioId, hoy)
                }

                // 2. actualizar el estado de la pantalla
                _uiState.update {
                    it.copy(resultado = resultado, cargando = false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(cargando = false, mensajeError = R.string.cumplimiento_error_cargar)
                }
            }
        }
    }
}
