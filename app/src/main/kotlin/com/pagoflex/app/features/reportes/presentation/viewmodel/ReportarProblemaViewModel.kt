package com.pagoflex.app.features.reportes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.data.ResultadoReporte
import com.pagoflex.app.features.reportes.presentation.model.ReportarProblemaUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ReportarProblemaViewModel(
    private val reportesRepository: ReportesRepository,
    private val ahora: () -> LocalDateTime = LocalDateTime::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportarProblemaUiState())
    val uiState: StateFlow<ReportarProblemaUiState> = _uiState.asStateFlow()

    fun cargar(usuarioId: Int, folio: String) {
        viewModelScope.launch {
            try {
                // 1. solo compromisos del propio usuario
                val hoy = ahora().toLocalDate()
                val compromiso = reportesRepository.obtenerCompromisoDeUsuario(usuarioId, folio, hoy)

                // 2. si ya tiene un reporte sin resolver se muestra ese
                val activo = compromiso?.let { reportesRepository.obtenerReporteActivo(folio, hoy) }

                _uiState.update {
                    it.copy(cargando = false, compromiso = compromiso, reporteActivo = activo, error = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, error = R.string.rep_error_cargar) }
            }
        }
    }

    fun setMotivo(motivo: MotivoReporte) {
        _uiState.update { it.copy(motivo = motivo, error = null) }
    }

    fun setComentario(comentario: String) {
        _uiState.update { it.copy(comentario = comentario, error = null) }
    }

    fun enviar(usuarioId: Int) {
        // 1. congelar estado y cancelar si ya se está enviando
        val estado = uiState.value
        val folio = estado.compromiso?.folio ?: return
        if (estado.enviando) return

        _uiState.update { it.copy(enviando = true, error = null) }

        viewModelScope.launch {
            try {
                // 2. reportar con las reglas de RN-09
                when (val resultado = reportesRepository.reportar(
                    folio, usuarioId, estado.motivo, estado.comentario, ahora()
                )) {
                    is ResultadoReporte.Creado -> {
                        _uiState.update { it.copy(recienEnviado = true) }
                        cargar(usuarioId, folio)
                    }

                    is ResultadoReporte.YaExiste -> cargar(usuarioId, folio)

                    is ResultadoReporte.Error -> _uiState.update { it.copy(error = resultado.mensaje) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(error = R.string.rep_error_generico) }
            } finally {
                _uiState.update { it.copy(enviando = false) }
            }
        }
    }
}
