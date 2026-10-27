package com.pagoflex.app.features.cobranza.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.presentation.model.CompromisoEmpresa
import com.pagoflex.app.features.cobranza.presentation.model.CompromisosEmpresaUiState
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CompromisosEmpresaViewModel(
    private val cobranzaRepository: CobranzaRepository,
    private val hoy: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompromisosEmpresaUiState(mes = YearMonth.from(hoy())))
    val uiState: StateFlow<CompromisosEmpresaUiState> = _uiState.asStateFlow()

    // todos los compromisos de la empresa, se filtran en memoria al cambiar mes o estado
    private var todos: List<CompromisoEmpresa> = emptyList()

    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            try {
                // 1. obtener ejecutivo y compromisos de su empresa (RN-22)
                val ejecutivo = cobranzaRepository.obtenerEjecutivo(usuarioId)
                todos = ejecutivo?.idEmpresa?.let { cobranzaRepository.obtenerCompromisos(it, hoy()) }.orEmpty()

                // 2. mostrar el mes y filtro actuales
                _uiState.update { it.copy(cargando = false, ejecutivo = ejecutivo, error = null) }
                aplicarFiltros()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, error = R.string.cob_error_cargar) }
            }
        }
    }

    fun mesAnterior() {
        _uiState.update { it.copy(mes = it.mes.minusMonths(1)) }
        aplicarFiltros()
    }

    fun mesSiguiente() {
        _uiState.update { it.copy(mes = it.mes.plusMonths(1)) }
        aplicarFiltros()
    }

    fun filtrar(estado: EstadoCompromiso?) {
        _uiState.update { it.copy(filtro = estado) }
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val estado = uiState.value

        // primero lo que requiere acción: en revisión, vencidos y pendientes
        val lista = todos
            .filter { YearMonth.from(it.fechaVencimiento) == estado.mes }
            .filter { estado.filtro == null || it.estado == estado.filtro }
            .sortedWith(compareBy({ ordenEstado(it.estado) }, { it.fechaVencimiento }, { it.folio }))

        _uiState.update { it.copy(compromisos = lista) }
    }

    private fun ordenEstado(estado: EstadoCompromiso) = when (estado) {
        EstadoCompromiso.EN_REVISION -> 0
        EstadoCompromiso.VENCIDO -> 1
        EstadoCompromiso.PENDIENTE -> 2
        EstadoCompromiso.PAGADO -> 3
        EstadoCompromiso.ANULADO -> 4
    }
}
