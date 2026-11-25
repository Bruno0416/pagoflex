package com.pagoflex.app.features.mensajeria.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.features.mensajeria.data.remote.ConversacionDto
import com.pagoflex.app.features.mensajeria.presentation.model.BandejaMensajesUiState
import com.pagoflex.app.features.mensajeria.presentation.model.ConversacionActiva
import com.pagoflex.app.features.reportes.presentation.model.ReporteItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BandejaMensajesViewModel(
    private val reportes: suspend () -> List<ReporteItem>,
    private val conversacion: suspend (String) -> ConversacionDto?,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BandejaMensajesUiState())
    val uiState: StateFlow<BandejaMensajesUiState> = _uiState.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val activos = reportes().filter { it.estado == EstadoReporte.EN_GESTION }
                val conversaciones = activos.map { reporte ->
                    async { reporte to conversacion(reporte.numero) }
                }.awaitAll().map { (reporte, chat) -> aConversacion(reporte, chat) }
                _uiState.update { it.copy(cargando = false, conversaciones = ordenar(conversaciones)) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, error = R.string.msj_error_cargar) }
            }
        }
    }

    companion object {
        fun aConversacion(reporte: ReporteItem, chat: ConversacionDto?): ConversacionActiva {
            val mensajes = chat?.ultimaSecuencia ?: 0L
            val leidos = chat?.ultimaSecuenciaLeida ?: 0L
            return ConversacionActiva(
                numero = reporte.numero,
                folio = reporte.folio,
                concepto = reporte.concepto,
                empresa = reporte.empresa,
                usuario = reporte.usuario,
                mensajes = mensajes,
                sinLeer = (mensajes - leidos).coerceAtLeast(0),
            )
        }

        fun ordenar(lista: List<ConversacionActiva>): List<ConversacionActiva> =
            lista.sortedWith(compareByDescending<ConversacionActiva> { it.sinLeer }.thenBy { it.numero })
    }
}
