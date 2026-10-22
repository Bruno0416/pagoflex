package com.pagoflex.app.features.cobranza.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.core.rules.MotivoAnulacion
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.data.EjecutivoSesion
import com.pagoflex.app.features.cobranza.presentation.model.DetalleCompromisoUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class DetalleCompromisoViewModel(
    private val cobranzaRepository: CobranzaRepository,
    private val ahora: () -> LocalDateTime = LocalDateTime::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleCompromisoUiState())
    val uiState: StateFlow<DetalleCompromisoUiState> = _uiState.asStateFlow()

    private var ejecutivo: EjecutivoSesion? = null

    fun cargar(usuarioId: Int, folio: String) {
        viewModelScope.launch {
            try {
                // 1. obtener ejecutivo y el compromiso solo si es de su empresa (RN-22)
                val actual = cobranzaRepository.obtenerEjecutivo(usuarioId).also { ejecutivo = it }
                val compromiso = actual?.idEmpresa?.let {
                    cobranzaRepository.obtenerDetalle(it, folio, ahora().toLocalDate())
                }

                // 2. reporte del usuario en modo lectura si está en revisión
                val reporte = compromiso?.let { cobranzaRepository.obtenerReporteActivo(folio) }

                _uiState.update {
                    it.copy(cargando = false, compromiso = compromiso, reporte = reporte, error = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, error = R.string.cob_error_cargar) }
            }
        }
    }

    // ---------- hoja de anulación ----------

    fun abrirAnulacion() {
        _uiState.update {
            it.copy(hojaAnulacionVisible = true, motivo = null, comentario = "", errorAnulacion = null)
        }
    }

    fun cerrarAnulacion() {
        _uiState.update { it.copy(hojaAnulacionVisible = false) }
    }

    fun setMotivo(motivo: MotivoAnulacion) {
        _uiState.update { it.copy(motivo = motivo, errorAnulacion = null) }
    }

    fun setComentario(comentario: String) {
        _uiState.update { it.copy(comentario = comentario, errorAnulacion = null) }
    }

    fun confirmarAnulacion(usuarioId: Int) {
        // 1. congelar estado y cancelar si ya está anulando
        val estado = uiState.value
        val folio = estado.compromiso?.folio ?: return
        val actual = ejecutivo ?: return
        if (estado.anulando) return

        _uiState.update { it.copy(anulando = true) }

        viewModelScope.launch {
            try {
                // 2. anular con las reglas de RN-21
                val error = cobranzaRepository.anular(folio, actual, estado.motivo, estado.comentario, ahora())

                if (error != null) {
                    _uiState.update { it.copy(errorAnulacion = error) }
                } else {
                    // 3. cerrar la hoja, avisar y recargar el detalle
                    _uiState.update { it.copy(hojaAnulacionVisible = false, mensaje = R.string.cob_anulado_ok) }
                    cargar(usuarioId, folio)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorAnulacion = R.string.cob_error_generico) }
            } finally {
                _uiState.update { it.copy(anulando = false) }
            }
        }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
