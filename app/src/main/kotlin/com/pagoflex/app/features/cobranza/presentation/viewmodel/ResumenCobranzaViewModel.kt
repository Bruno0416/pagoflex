package com.pagoflex.app.features.cobranza.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.core.rules.CalculoCobranza
import com.pagoflex.app.core.rules.ItemCobranza
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.presentation.model.ResumenCobranzaUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ResumenCobranzaViewModel(
    private val cobranzaRepository: CobranzaRepository,
    private val hoy: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResumenCobranzaUiState(mes = YearMonth.from(hoy())))
    val uiState: StateFlow<ResumenCobranzaUiState> = _uiState.asStateFlow()

    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            try {
                // 1. obtener ejecutivo y su empresa
                val ejecutivo = cobranzaRepository.obtenerEjecutivo(usuarioId)
                val empresaId = ejecutivo?.idEmpresa

                // 2. obtener compromisos solo de su empresa (RN-22)
                val compromisos = if (empresaId != null) {
                    cobranzaRepository.obtenerCompromisos(empresaId, hoy())
                } else {
                    emptyList()
                }

                // 3. calcular el resumen del mes seleccionado (RN-24)
                val mes = uiState.value.mes
                val resumen = CalculoCobranza.calcularResumen(
                    compromisos.map { ItemCobranza(it.fechaVencimiento, it.estado, it.pago?.monto) },
                    mes,
                    hoy()
                )

                _uiState.update {
                    it.copy(cargando = false, ejecutivo = ejecutivo, resumen = resumen, error = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, error = R.string.cob_error_cargar) }
            }
        }
    }

    fun mesAnterior(usuarioId: Int) {
        _uiState.update { it.copy(mes = it.mes.minusMonths(1)) }
        cargar(usuarioId)
    }

    fun mesSiguiente(usuarioId: Int) {
        _uiState.update { it.copy(mes = it.mes.plusMonths(1)) }
        cargar(usuarioId)
    }
}
