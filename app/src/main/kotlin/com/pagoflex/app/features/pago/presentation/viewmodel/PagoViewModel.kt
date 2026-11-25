package com.pagoflex.app.features.pago.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.pago.data.MetodoPagoModel
import com.pagoflex.app.features.pago.data.MotivoRechazoPago
import com.pagoflex.app.features.pago.data.PagoRechazado
import com.pagoflex.app.features.pago.data.PagoRepository
import com.pagoflex.app.features.pago.presentation.model.PagoUiState
import com.pagoflex.app.features.pago.presentation.model.PasoPago
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PagoViewModel(
    private val repository: PagoRepository,
    private val scope: CoroutineScope? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(PagoUiState())
    val uiState = _uiState.asStateFlow()
    private var preparacion: Job? = null
    private var llaveIntento: String? = null
    private var sesionHoja: String? = null

    fun iniciarPago(folio: String, sesionId: String) {
        // 1. evitar reiniciar una hoja de pago ya abierta
        if (sesionHoja == sesionId) return
        sesionHoja = sesionId
        // 2. limpiar el intento anterior y solicitar una cotizacion
        preparacion?.cancel()
        llaveIntento = null
        _uiState.value = PagoUiState()
        preparacion = (scope ?: viewModelScope).launch { cargarCotizacion(folio) }
    }

    private suspend fun cargarCotizacion(folio: String) {
        // 1. obtener el total y la cotizacion vigentes
        try {
            val resumen = withContext(Dispatchers.IO) { repository.prepararPago(folio) }
            _uiState.update { it.copy(cargando = false, resumen = resumen) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: PagoRechazado) {
            // informar si el compromiso no puede cotizarse
            mostrarError(e)
        } catch (_: Exception) {
            // informar si el compromiso no puede cotizarse
            establecerError(R.string.pago_error_general)
        }
    }

    fun seleccionarMetodo(metodo: MetodoPagoModel) {
        _uiState.update {
            if (it.cargando || it.procesando || it.resultadoIncierto || it.comprobanteConfirmado != null) it
            else it.copy(metodo = metodo, error = null)
        }
    }

    fun avanzarAConfirmacion() {
        _uiState.update {
            if (it.cargando || it.procesando || it.metodo == null || it.resumen == null ||
                it.comprobanteConfirmado != null) it
            else it.copy(paso = PasoPago.CONFIRMACION, error = null)
        }
    }

    fun volver() {
        _uiState.update {
            if (it.procesando || it.resultadoIncierto || it.comprobanteConfirmado != null) it
            else it.copy(paso = PasoPago.METODO, error = null)
        }
    }

    fun confirmarPago() {
        // 1. validar que el pago puede confirmarse
        val estado = _uiState.value
        if (estado.procesando || estado.cargando || estado.paso != PasoPago.CONFIRMACION ||
            estado.comprobanteConfirmado != null) return
        val resumen = estado.resumen ?: return
        val metodo = estado.metodo ?: return

        // 2. conservar la llave de idempotencia entre reintentos
        val llave = llaveIntento ?: UUID.randomUUID().toString().also { llaveIntento = it }
        _uiState.update { it.copy(procesando = true, error = null) }
        // 3. confirmar el pago con los datos de la cotizacion
        (scope ?: viewModelScope).launch {
            try {
                val comprobante = withContext(Dispatchers.IO) {
                    repository.confirmarPago(resumen, metodo, llave)
                }
                // 4. mostrar el comprobante devuelto por el backend
                _uiState.update {
                    it.copy(procesando = false, resultadoIncierto = false,
                        comprobanteConfirmado = comprobante)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: PagoRechazado) {
                if (e.motivo == MotivoRechazoPago.MONTO_CAMBIO) {
                    // 4. pedir nueva cotizacion y exigir otra confirmacion
                    llaveIntento = null
                    _uiState.update {
                        it.copy(procesando = false, cargando = true, resumen = null,
                            paso = PasoPago.METODO, resultadoIncierto = false, error = null)
                    }
                    cargarCotizacion(resumen.folio)
                    if (_uiState.value.resumen != null) {
                        _uiState.update { it.copy(error = R.string.pago_error_cambio) }
                    }
                } else {
                    mostrarError(e)
                }
            } catch (_: Exception) {
                // 4. conservar la misma llave si el resultado es incierto
                _uiState.update {
                    it.copy(procesando = false, resultadoIncierto = true,
                        error = R.string.pago_error_conexion)
                }
            }
        }
    }

    private fun mostrarError(error: PagoRechazado) {
        // 1. convertir el motivo rechazado en un recurso de mensaje
        val recurso = when (error.motivo) {
            MotivoRechazoPago.NO_DISPONIBLE -> R.string.pago_error_no_disponible
            MotivoRechazoPago.EN_REVISION -> R.string.pago_error_revision
            MotivoRechazoPago.ANULADO -> R.string.pago_error_anulado
            MotivoRechazoPago.YA_PAGADO -> R.string.pago_error_pagado
            MotivoRechazoPago.ORDEN -> R.string.pago_error_orden
            MotivoRechazoPago.MONTO_CAMBIO -> R.string.pago_error_cambio
            MotivoRechazoPago.MEDIO_INVALIDO -> R.string.pago_error_medio
        }
        // 2. restaurar el formulario sin confirmar el pago
        _uiState.update {
            it.copy(cargando = false, procesando = false, resultadoIncierto = false,
                resumen = null, paso = PasoPago.METODO,
                error = recurso, folioBloqueante = error.folioAnterior)
        }
    }

    private fun establecerError(@StringRes error: Int) {
        _uiState.update { it.copy(cargando = false, procesando = false, error = error) }
    }
}
