package com.pagoflex.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.data.remote.dto.request.RecuperacionRequest
import com.pagoflex.app.features.auth.data.remote.dto.request.RecuperarContrasenaRequest
import com.pagoflex.app.features.auth.data.remote.dto.request.VerificarCodigoRequest
import com.pagoflex.app.features.auth.presentation.model.PasoRecuperacion
import com.pagoflex.app.features.auth.presentation.model.RecuperarContrasenaUiState
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RecuperarContrasenaViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RecuperarContrasenaUiState())
    val uiState: StateFlow<RecuperarContrasenaUiState> = _uiState
    private var tokenRestablecimiento: String? = null

    fun setCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorRecuperacion = null) }
    }

    fun validarCorreoField() {
        _uiState.update { it.copy(errorCorreo = AuthValidators.validarCorreo(it.correo)) }
    }

    fun solicitarCodigo() {
        val estado = _uiState.value
        if (estado.cargando || estado.paso != PasoRecuperacion.CORREO) return
        val error = AuthValidators.validarCorreo(estado.correo)
        if (error != null) {
            _uiState.update { it.copy(errorCorreo = error) }
            return
        }
        _uiState.update { it.copy(cargando = true, errorRecuperacion = null) }
        viewModelScope.launch {
            try {
                authRepository.solicitarRecuperacion(RecuperacionRequest(estado.correo.trim()))
                _uiState.update {
                    it.copy(
                        correo = estado.correo.trim(),
                        paso = PasoRecuperacion.CODIGO,
                        codigo = "",
                        errorCodigo = null,
                        codigoReenviado = false,
                        requiereNuevoCodigo = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_api_error_red) }
            } catch (e: HttpException) {
                _uiState.update { it.copy(errorRecuperacion = errorHttp(e)) }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_recuperacion_error_general) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun setCodigo(codigo: String) {
        val normalizado = codigo.uppercase().filter { it in 'A'..'Z' || it in '0'..'9' }.take(6)
        _uiState.update {
            it.copy(codigo = normalizado, errorCodigo = null, errorRecuperacion = null)
        }
    }

    fun reenviarCodigo() {
        val estado = _uiState.value
        if (estado.cargando || estado.paso != PasoRecuperacion.CODIGO) return
        _uiState.update { it.copy(cargando = true, errorRecuperacion = null, codigoReenviado = false) }
        viewModelScope.launch {
            try {
                authRepository.reenviarCodigo(RecuperacionRequest(estado.correo))
                _uiState.update {
                    it.copy(codigoReenviado = true, requiereNuevoCodigo = false,
                        codigo = "", errorCodigo = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_api_error_red) }
            } catch (e: HttpException) {
                _uiState.update { it.copy(errorRecuperacion = errorHttp(e)) }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_recuperacion_error_general) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun verificarCodigo() {
        val estado = _uiState.value
        if (estado.cargando || estado.paso != PasoRecuperacion.CODIGO) return
        if (estado.requiereNuevoCodigo) return
        if (estado.codigo.length != 6) {
            _uiState.update { it.copy(errorCodigo = R.string.auth_api_codigo_longitud) }
            return
        }
        _uiState.update { it.copy(cargando = true, errorRecuperacion = null) }
        viewModelScope.launch {
            try {
                val respuesta = authRepository.verificarCodigoRecuperacion(
                    VerificarCodigoRequest(estado.correo, estado.codigo.trim())
                )
                check(respuesta.tokenRestablecimiento.isNotBlank())
                tokenRestablecimiento = respuesta.tokenRestablecimiento
                _uiState.update {
                    it.copy(
                        paso = PasoRecuperacion.CONTRASENA,
                        codigo = "",
                        errorCodigo = null,
                        errorRecuperacion = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_api_error_red) }
            } catch (e: HttpException) {
                _uiState.update { it.copy(errorRecuperacion = errorHttp(e, R.string.auth_api_codigo_invalido)) }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_recuperacion_error_general) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun cambiarCorreo() {
        if (_uiState.value.cargando) return
        tokenRestablecimiento = null
        _uiState.value = RecuperarContrasenaUiState(correo = _uiState.value.correo)
    }

    fun volverPasoAnterior() {
        val estado = _uiState.value
        if (estado.cargando) return
        when (estado.paso) {
            PasoRecuperacion.CORREO -> Unit
            PasoRecuperacion.CODIGO -> cambiarCorreo()
            PasoRecuperacion.CONTRASENA -> {
                tokenRestablecimiento = null
                _uiState.update {
                    it.copy(
                        paso = PasoRecuperacion.CODIGO,
                        codigo = "",
                        nuevaContrasena = "",
                        confirmarContrasena = "",
                        errorCodigo = null,
                        errorContrasena = null,
                        errorConfirmacion = null,
                        errorRecuperacion = null,
                        codigoReenviado = false,
                        requiereNuevoCodigo = true
                    )
                }
            }
            PasoRecuperacion.EXITO -> reiniciar()
        }
    }

    fun reiniciar() {
        if (_uiState.value.cargando) return
        tokenRestablecimiento = null
        _uiState.value = RecuperarContrasenaUiState()
    }

    fun setNuevaContrasena(contrasena: String) {
        _uiState.update {
            it.copy(nuevaContrasena = contrasena, errorContrasena = null, errorRecuperacion = null)
        }
    }

    fun validarContrasenaField() {
        _uiState.update { it.copy(errorContrasena = AuthValidators.validarContrasenaSegura(it.nuevaContrasena)) }
    }

    fun setConfirmarContrasena(contrasena: String) {
        _uiState.update {
            it.copy(confirmarContrasena = contrasena, errorConfirmacion = null, errorRecuperacion = null)
        }
    }

    fun validarConfirmacionField() {
        _uiState.update {
            it.copy(errorConfirmacion = AuthValidators.validarConfirmarContrasena(
                it.nuevaContrasena, it.confirmarContrasena
            ))
        }
    }

    fun restablecerContrasena() {
        val estado = _uiState.value
        val token = tokenRestablecimiento
        if (estado.cargando || estado.paso != PasoRecuperacion.CONTRASENA || token == null) return
        val errorContrasena = AuthValidators.validarContrasenaSegura(estado.nuevaContrasena)
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
                authRepository.restablecerContrasena(
                    RecuperarContrasenaRequest(token, estado.nuevaContrasena)
                )
                tokenRestablecimiento = null
                _uiState.update {
                    it.copy(
                        paso = PasoRecuperacion.EXITO,
                        nuevaContrasena = "",
                        confirmarContrasena = "",
                        codigo = ""
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: IOException) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_api_error_red) }
            } catch (e: HttpException) {
                val campos = authRepository.camposInvalidos(e).keys
                val contrasenaInvalida = "nuevaContrasena" in campos
                _uiState.update {
                    it.copy(
                        errorContrasena = if (contrasenaInvalida)
                            R.string.auth_error_contrasena_segura else null,
                        errorRecuperacion = if (contrasenaInvalida && campos.size == 1)
                            null else errorHttp(e)
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorRecuperacion = R.string.auth_recuperacion_error_general) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    private fun errorHttp(e: HttpException, errorPorCodigo: Int? = null): Int =
        when (e.code()) {
            429 -> R.string.auth_api_muchas_solicitudes
            400, 401, 404, 410 -> errorPorCodigo ?: R.string.auth_recuperacion_error_general
            else -> R.string.auth_recuperacion_error_general
        }
}
