package com.pagoflex.app.features.pago.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.pago.data.MetodoPago
import com.pagoflex.app.features.pago.data.MetodoPagoPreferences
import com.pagoflex.app.features.pago.data.MotivoRechazoPago
import com.pagoflex.app.features.pago.data.PagoRechazado
import com.pagoflex.app.features.pago.data.PagoRepository
import com.pagoflex.app.features.pago.presentation.model.PagoUiState
import com.pagoflex.app.features.pago.presentation.model.PasoPago
import com.pagoflex.app.features.pago.presentation.validators.PagoValidators
import com.pagoflex.app.features.reconocimientos.data.ReconocimientosRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PagoViewModel(
    private val repository: PagoRepository,
    private val preferences: MetodoPagoPreferences,
    private val reconocimientosRepository: ReconocimientosRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PagoUiState())
    val uiState = _uiState.asStateFlow()
    private var preparacion: Job? = null

    // ---------- preparacion del pago ----------
    fun iniciarPago(folio: String, usuarioId: Int) {
        // 1. cancelar cualquier preparacion anterior y reiniciar el estado
        preparacion?.cancel()
        _uiState.value = PagoUiState()
        preparacion = viewModelScope.launch {
            try {
                // 2. cargar el resumen actualizado del compromiso
                val resumen = withContext(Dispatchers.IO) {
                    repository.prepararPago(folio, usuarioId)
                }
                _uiState.update { it.copy(cargando = false, resumen = resumen) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: PagoRechazado) {
                // 3. presentar los errores de preparacion
                mostrarError(e)
            } catch (_: Exception) {
                // 3. presentar los errores de preparacion
                establecerError(R.string.pago_error_general)
            }
        }
    }

    // ---------- selección del metodo ----------
    fun seleccionarMetodo(metodo: MetodoPago, usuarioId: Int) {
        if (_uiState.value.procesando) return
        // 1. consultar si existe una preferencia guardada para el medio
        val identificador = preferences.obtenerIdentificador(usuarioId, metodo)
        // 2. actualizar la selección y limpiar los datos de tarjeta anteriores
        _uiState.update {
            it.copy(
                metodo = metodo,
                identificadorGuardado = identificador,
                usarGuardado = identificador != null,
                numeroTarjeta = "",
                vencimientoTarjeta = "",
                codigoSeguridad = "",
                recordar = false,
                errorNumeroTarjeta = null,
                errorVencimientoTarjeta = null,
                errorCodigoSeguridad = null,
                error = null
            )
        }
    }

    fun avanzarADatos() {
        val estado = _uiState.value
        if (estado.cargando || estado.metodo == null) return
        _uiState.update { it.copy(paso = PasoPago.DATOS, error = null) }
    }

    fun usarMetodoGuardado(usar: Boolean) {
        _uiState.update {
            it.copy(
                usarGuardado = usar && it.identificadorGuardado != null,
                errorNumeroTarjeta = null,
                errorVencimientoTarjeta = null,
                errorCodigoSeguridad = null,
                error = null
            )
        }
    }

    // --- numero de tarjeta ---
    fun cambiarNumeroTarjeta(valor: String) {
        val numeros = valor.filter(Char::isDigit).take(16)
        _uiState.update { it.copy(numeroTarjeta = numeros, errorNumeroTarjeta = null) }
    }

    fun validarNumeroTarjetaField() {
        val error = PagoValidators.validarNumeroTarjeta(uiState.value.numeroTarjeta)
        _uiState.update { it.copy(errorNumeroTarjeta = error) }
    }

    // --- vencimiento de tarjeta ---
    fun cambiarVencimientoTarjeta(valor: String) {
        // 1. conservar solo los dígitos para mantener estable el cursor
        val numeros = valor.filter(Char::isDigit).take(4)
        _uiState.update { it.copy(vencimientoTarjeta = numeros, errorVencimientoTarjeta = null) }
    }

    fun validarVencimientoTarjetaField() {
        val error = PagoValidators.validarVencimientoTarjeta(uiState.value.vencimientoTarjeta)
        _uiState.update { it.copy(errorVencimientoTarjeta = error) }
    }

    // --- codigo de seguridad ---
    fun cambiarCodigoSeguridad(valor: String) {
        val numeros = valor.filter(Char::isDigit).take(4)
        _uiState.update { it.copy(codigoSeguridad = numeros, errorCodigoSeguridad = null) }
    }

    fun validarCodigoSeguridadField() {
        val error = PagoValidators.validarCodigoSeguridad(uiState.value.codigoSeguridad)
        _uiState.update { it.copy(errorCodigoSeguridad = error) }
    }

    // ---------- opciones del formulario ----------
    fun limpiarDatosTarjeta() {
        if (_uiState.value.procesando) return
        _uiState.update {
            it.copy(
                numeroTarjeta = "", vencimientoTarjeta = "", codigoSeguridad = "",
                errorNumeroTarjeta = null, errorVencimientoTarjeta = null,
                errorCodigoSeguridad = null
            )
        }
    }

    fun cambiarRecordar(valor: Boolean) {
        _uiState.update { it.copy(recordar = valor) }
    }

    fun avanzarAConfirmacion() {
        // 1. verificar que el medio y el compromiso estén disponibles
        val estado = _uiState.value
        if (estado.metodo == null || estado.resumen == null || estado.procesando) return

        // 2. validar cada campo si se ingresa una tarjeta nueva
        if (!estado.usarGuardado && estado.metodo != MetodoPago.TRANSFERENCIA) {
            val errorNumero = PagoValidators.validarNumeroTarjeta(estado.numeroTarjeta)
            val errorVencimiento = PagoValidators.validarVencimientoTarjeta(estado.vencimientoTarjeta)
            val errorCodigo = PagoValidators.validarCodigoSeguridad(estado.codigoSeguridad)

            // 3. mostrar los errores y mantener el formulario si hay datos inválidos
            if (errorNumero != null || errorVencimiento != null || errorCodigo != null) {
                _uiState.update {
                    it.copy(
                        errorNumeroTarjeta = errorNumero,
                        errorVencimientoTarjeta = errorVencimiento,
                        errorCodigoSeguridad = errorCodigo
                    )
                }
                return
            }
        }

        // 4. avanzar al resumen cuando los campos sean válidos
        _uiState.update { it.copy(paso = PasoPago.CONFIRMACION, error = null) }
    }

    fun volver() {
        if (_uiState.value.procesando) return
        _uiState.update {
            it.copy(
                paso = when (it.paso) {
                    PasoPago.CONFIRMACION -> PasoPago.DATOS
                    PasoPago.DATOS, PasoPago.METODO -> PasoPago.METODO
                },
                error = null
            )
        }
    }

    // ---------- confirmación del pago ----------
    fun confirmarPago(usuarioId: Int) {
        val estado = _uiState.value
        if (estado.procesando || estado.paso != PasoPago.CONFIRMACION) return
        val resumen = estado.resumen ?: return
        val metodo = estado.metodo ?: return

        // 1. impedir confirmaciones duplicadas antes de lanzar la corrutina
        _uiState.update { it.copy(procesando = true, error = null) }
        viewModelScope.launch {
            try {
                // 2. confirmar el pago simulado en la base local
                val numero = withContext(Dispatchers.IO) {
                    repository.confirmarPago(usuarioId, resumen, metodo)
                }
                // 3. recordar la preferencia solo después de confirmar el pago
                if (!estado.usarGuardado && estado.recordar) {
                    try {
                        preferences.recordar(usuarioId, metodo, estado.numeroTarjeta)
                    } catch (_: Exception) {
                        // 3. conservar el pago confirmado si falla la preferencia
                    }
                }
                // 4. descartar los datos introducidos y publicar el comprobante
                _uiState.update {
                    it.copy(
                        procesando = false,
                        numeroTarjeta = "",
                        vencimientoTarjeta = "",
                        codigoSeguridad = "",
                        errorNumeroTarjeta = null,
                        errorVencimientoTarjeta = null,
                        errorCodigoSeguridad = null,
                        comprobanteConfirmado = numero
                    )
                }

                // 5. evaluar los logros sin afectar un pago confirmado
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        reconocimientosRepository.evaluar(usuarioId)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        // los logros se recuperan al volver a la aplicacion
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: PagoRechazado) {
                // 6. informar el rechazo de la confirmación
                mostrarError(e)
            } catch (_: Exception) {
                // 6. informar el rechazo de la confirmación
                establecerError(R.string.pago_error_general)
            }
        }
    }

    fun consumirComprobante() {
        _uiState.update { it.copy(comprobanteConfirmado = null) }
    }

    // --- presentar error del pago ---
    private fun mostrarError(error: PagoRechazado) {
        // 1. resolver el mensaje correspondiente al rechazo
        val recurso = when (error.motivo) {
            MotivoRechazoPago.NO_DISPONIBLE -> R.string.pago_error_no_disponible
            MotivoRechazoPago.EN_REVISION -> R.string.pago_error_revision
            MotivoRechazoPago.ANULADO -> R.string.pago_error_anulado
            MotivoRechazoPago.YA_PAGADO -> R.string.pago_error_pagado
            MotivoRechazoPago.ORDEN -> R.string.pago_error_orden
            MotivoRechazoPago.MONTO_CAMBIO -> R.string.pago_error_cambio
            MotivoRechazoPago.MEDIO_INVALIDO -> R.string.pago_error_medio
        }
        // 2. actualizar el estado con el error y el folio bloqueante
        _uiState.update {
            it.copy(
                cargando = false, procesando = false,
                error = recurso, folioBloqueante = error.folioAnterior
            )
        }
    }

    // --- establecer error general ---
    private fun establecerError(@StringRes error: Int) {
        _uiState.update { it.copy(cargando = false, procesando = false, error = error) }
    }
}
