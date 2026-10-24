package com.pagoflex.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.presentation.model.RecuperarContrasenaUiState
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecuperarContrasenaViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RecuperarContrasenaUiState())
    val uiState: StateFlow<RecuperarContrasenaUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(preguntas = authRepository.obtenerPreguntas()) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_recuperacion_error_general) }
            }
        }
    }

    // ---------- paso 1: validar identidad ----------
    fun setCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorRecuperacion = null) }
    }

    fun validarCorreoField() {
        _uiState.update { it.copy(errorCorreo = AuthValidators.validarCorreo(it.correo)) }
    }

    fun setPreguntaRecuperacion(idPregunta: Int) {
        _uiState.update {
            it.copy(
                idPreguntaRecuperacion = idPregunta,
                errorPregunta = null,
                errorRecuperacion = null
            )
        }
    }

    fun setRespuesta(respuesta: String) {
        _uiState.update {
            it.copy(
                respuesta = respuesta,
                errorRespuesta = null,
                errorRecuperacion = null
            )
        }
    }

    fun validarRespuestaField() {
        _uiState.update { it.copy(errorRespuesta = AuthValidators.validarRespuestaRecuperacion(it.respuesta)) }
    }

    fun validarIdentidad() {
        val estado = uiState.value
        if (estado.cargando || estado.identidadValidada) return

        val errorCorreo = AuthValidators.validarCorreo(estado.correo)
        val errorPregunta =
            AuthValidators.validarPreguntaRecuperacion(estado.idPreguntaRecuperacion)
        val errorRespuesta = AuthValidators.validarRespuestaRecuperacion(estado.respuesta)
        if (errorCorreo != null || errorPregunta != null || errorRespuesta != null) {
            _uiState.update {
                it.copy(
                    errorCorreo = errorCorreo,
                    errorPregunta = errorPregunta,
                    errorRespuesta = errorRespuesta
                )
            }
            return
        }

        _uiState.update { it.copy(cargando = true, errorRecuperacion = null) }
        viewModelScope.launch {
            try {
                val exito = authRepository.validarIdentidadRecuperacion(
                    estado.correo, requireNotNull(estado.idPreguntaRecuperacion), estado.respuesta
                )
                if (exito) {
                    _uiState.update {
                        it.copy(identidadValidada = true, correo = estado.correo.trim().lowercase())
                    }
                } else {
                    _uiState.update { it.copy(errorRecuperacion = R.string.auth_recuperacion_error_respuesta) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_recuperacion_error_general) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun cambiarDatos() {
        _uiState.update {
            it.copy(
                identidadValidada = false,
                respuesta = "",
                nuevaContrasena = "",
                confirmarContrasena = "",
                errorRespuesta = null,
                errorContrasena = null,
                errorConfirmacion = null,
                errorRecuperacion = null
            )
        }
    }

    // ---------- paso 2: nueva contrasena ----------
    fun setNuevaContrasena(contrasena: String) {
        _uiState.update {
            it.copy(
                nuevaContrasena = contrasena,
                errorContrasena = null,
                errorRecuperacion = null
            )
        }
    }

    fun validarContrasenaField() {
        _uiState.update { it.copy(errorContrasena = AuthValidators.validarContrasena(it.nuevaContrasena)) }
    }

    fun setConfirmarContrasena(contrasena: String) {
        _uiState.update {
            it.copy(
                confirmarContrasena = contrasena,
                errorConfirmacion = null,
                errorRecuperacion = null
            )
        }
    }

    fun validarConfirmacionField() {
        _uiState.update {
            it.copy(
                errorConfirmacion = AuthValidators.validarConfirmarContrasena(
                    it.nuevaContrasena,
                    it.confirmarContrasena
                )
            )
        }
    }

    fun restablecerContrasena() {
        val estado = uiState.value
        if (estado.cargando || !estado.identidadValidada || estado.idPreguntaRecuperacion == null) return

        val errorContrasena = AuthValidators.validarContrasena(estado.nuevaContrasena)
        val errorConfirmacion = AuthValidators.validarConfirmarContrasena(
            estado.nuevaContrasena, estado.confirmarContrasena
        )
        if (errorContrasena != null || errorConfirmacion != null) {
            _uiState.update {
                it.copy(errorContrasena = errorContrasena, errorConfirmacion = errorConfirmacion)
            }
            return
        }

        _uiState.update { it.copy(cargando = true, errorRecuperacion = null) }
        viewModelScope.launch {
            try {
                val exito = authRepository.restablecerContrasena(
                    estado.correo, estado.idPreguntaRecuperacion,
                    estado.respuesta, estado.nuevaContrasena
                )
                if (exito) {
                    _uiState.update {
                        it.copy(
                            recuperacionExitosa = true,
                            respuesta = "",
                            nuevaContrasena = "",
                            confirmarContrasena = ""
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            identidadValidada = false,
                            respuesta = "",
                            nuevaContrasena = "",
                            confirmarContrasena = "",
                            errorRecuperacion = R.string.auth_recuperacion_error_respuesta
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_recuperacion_error_general) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
