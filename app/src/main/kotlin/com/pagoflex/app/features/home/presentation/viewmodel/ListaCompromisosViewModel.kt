package com.pagoflex.app.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.home.data.HomeRepository
import com.pagoflex.app.features.home.presentation.model.ListaCompromisosUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ListaCompromisosViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaCompromisosUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarCompromisos(usuarioId: Int) {
        if (usuarioId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            try {
                val compromisos = withContext(Dispatchers.IO) {
                    homeRepository.obtenerCompromisos(usuarioId, LocalDate.now())
                }
                _uiState.update {
                    it.copy(
                        compromisos = compromisos,
                        cargando = false,
                        mensajeError = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(cargando = false, mensajeError = R.string.home_error_cargar)
                }
            }
        }
    }
}
