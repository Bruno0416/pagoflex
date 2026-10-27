package com.pagoflex.app.features.reconocimientos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.reconocimientos.data.ReconocimientosRepository
import com.pagoflex.app.features.reconocimientos.presentation.model.ReconocimientosUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReconocimientosViewModel(
    private val repositorio: ReconocimientosRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReconocimientosUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarReconocimientos(usuarioId: Int) {
        if (usuarioId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            try {
                // 1. actualizar y consultar los logros reales del usuario
                val reconocimientos = withContext(Dispatchers.IO) {
                    repositorio.evaluarYObtener(usuarioId)
                }
                // 2. mostrar los reconocimientos guardados
                _uiState.update { it.copy(cargando = false, reconocimientos = reconocimientos) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(cargando = false, mensajeError = R.string.reconocimientos_error_cargar)
                }
            }
        }
    }
}
