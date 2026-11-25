package com.pagoflex.app.features.home.presentation.viewmodel

import android.util.Log
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

class ListaCompromisosViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ListaCompromisosUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarCompromisos() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            try {
                // 1. obtener y ordenar compromisos por vencimiento
                val compromisos = withContext(Dispatchers.IO) {
                    homeRepository.obtenerCompromisos()
                }.sortedBy { it.fechaVencimiento }
                // 2. publicar la lista obtenida
                _uiState.update {
                    it.copy(compromisos = compromisos, cargando = false, mensajeError = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // 2. informar si falla la consulta
                Log.e("PagoFlexHome", "No fue posible cargar la lista de compromisos", e)
                _uiState.update {
                    it.copy(cargando = false, mensajeError = R.string.home_error_cargar)
                }
            }
        }
    }
}
