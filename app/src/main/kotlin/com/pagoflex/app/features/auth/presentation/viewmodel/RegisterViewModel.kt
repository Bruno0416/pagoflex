package com.pagoflex.app.features.auth.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.data.remote.dto.request.RegistrarRequest
import com.pagoflex.app.features.auth.presentation.model.RegisterUiState
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun setRut(rut: String) {
        _uiState.update { it.copy(rut = rut, errorRut = null, errorRegistro = null) }
    }

    fun validarRutField() {
        _uiState.update { it.copy(errorRut = AuthValidators.validarRut(it.rut)) }
    }

    fun setNombres(nombres: String) {
        _uiState.update { it.copy(nombres = nombres, errorNombres = null, errorRegistro = null) }
    }

    fun validarNombresField() {
        _uiState.update { it.copy(errorNombres = AuthValidators.validarNombres(it.nombres)) }
    }

    fun setApellidos(apellidos: String) {
        _uiState.update { it.copy(apellidos = apellidos, errorApellidos = null, errorRegistro = null) }
    }

    fun validarApellidosField() {
        _uiState.update { it.copy(errorApellidos = AuthValidators.validarApellidos(it.apellidos)) }
    }

    fun setCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorRegistro = null) }
    }

    fun validarCorreoField() {
        _uiState.update { it.copy(errorCorreo = AuthValidators.validarCorreo(it.correo)) }
    }

    fun setContrasena(contrasena: String) {
        _uiState.update {
            it.copy(contrasena = contrasena, errorContrasena = null, errorRegistro = null)
        }
    }

    fun validarContrasenaField() {
        _uiState.update { it.copy(errorContrasena = AuthValidators.validarContrasenaSegura(it.contrasena)) }
    }

    fun setConfirmarContrasena(confirmarContrasena: String) {
        _uiState.update {
            it.copy(
                confirmarContrasena = confirmarContrasena,
                errorConfirmarContrasena = null,
                errorRegistro = null
            )
        }
    }

    fun validarConfirmarContrasenaField() {
        _uiState.update {
            it.copy(
                errorConfirmarContrasena = AuthValidators.validarConfirmarContrasena(
                    it.contrasena, it.confirmarContrasena
                )
            )
        }
    }

    fun setCodigoEmpresa(codigo: String) {
        _uiState.update {
            it.copy(codigoEmpresa = codigo, errorCodigoEmpresa = null, errorRegistro = null)
        }
    }

    fun setTerminosYCondiciones(value: Boolean) {
        _uiState.update {
            it.copy(
                terminosYCondiciones = value,
                errorTerminosYCondiciones = null,
                errorRegistro = null
            )
        }
    }

    fun registrar(onExito: () -> Unit) {
        val estado = _uiState.value
        if (estado.cargando) return
        val errorRut = AuthValidators.validarRut(estado.rut)
        val errorNombres = AuthValidators.validarNombres(estado.nombres)
        val errorApellidos = AuthValidators.validarApellidos(estado.apellidos)
        val errorCorreo = AuthValidators.validarCorreo(estado.correo)
        val errorContrasena = AuthValidators.validarContrasenaSegura(estado.contrasena)
        val errorConfirmacion = AuthValidators.validarConfirmarContrasena(
            estado.contrasena, estado.confirmarContrasena
        )
        if (errorRut != null || errorNombres != null || errorApellidos != null ||
            errorCorreo != null || errorContrasena != null || errorConfirmacion != null ||
            !estado.terminosYCondiciones
        ) {
            _uiState.update {
                it.copy(
                    errorRut = errorRut,
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorCorreo = errorCorreo,
                    errorContrasena = errorContrasena,
                    errorConfirmarContrasena = errorConfirmacion,
                    errorTerminosYCondiciones = if (estado.terminosYCondiciones) null
                    else R.string.auth_error_terminos_y_condiciones
                )
            }
            return
        }

        _uiState.update { it.copy(cargando = true, errorRegistro = null) }
        viewModelScope.launch {
            try {
                authRepository.registrarUsuario(
                    RegistrarRequest(
                        correo = estado.correo.trim(),
                        contrasena = estado.contrasena,
                        rut = AuthValidators.normalizarRut(estado.rut),
                        nombres = AuthValidators.normalizarNombre(estado.nombres),
                        apellidos = AuthValidators.normalizarNombre(estado.apellidos),
                        codigoEmpresa = estado.codigoEmpresa.trim().ifEmpty { null }
                    )
                )
                _uiState.update {
                    it.copy(contrasena = "", confirmarContrasena = "")
                }
                onExito()
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                val campos = authRepository.camposInvalidos(e).keys
                val reconocidos = setOf(
                    "rut", "nombres", "apellidos", "correo", "contrasena", "codigoEmpresa"
                )
                val errorGeneral = when (e.code()) {
                    400, 422 -> if (campos.isEmpty() || campos.any { it !in reconocidos })
                        R.string.auth_api_registro_datos_invalidos else null
                    409 -> if (campos.isEmpty() || campos.any { it !in reconocidos })
                        R.string.auth_api_registro_duplicado else null
                    429 -> R.string.auth_api_muchas_solicitudes
                    in 500..599 -> R.string.auth_api_registro_error_servidor
                    else -> R.string.auth_error_registro
                }
                Log.w("PagoFlexAuth", "Registro HTTP ${e.code()}; campos: $campos")
                _uiState.update {
                    it.copy(
                        errorRut = when {
                            "rut" !in campos -> null
                            e.code() == 409 -> R.string.auth_api_rut_duplicado
                            else -> R.string.auth_error_rut_invalido
                        },
                        errorNombres = if ("nombres" in campos)
                            R.string.auth_api_nombres_invalidos else null,
                        errorApellidos = if ("apellidos" in campos)
                            R.string.auth_api_apellidos_invalidos else null,
                        errorCorreo = when {
                            "correo" !in campos -> null
                            e.code() == 409 -> R.string.auth_api_correo_duplicado
                            else -> R.string.auth_error_correo_invalido
                        },
                        errorContrasena = if ("contrasena" in campos)
                            R.string.auth_error_contrasena_segura else null,
                        errorCodigoEmpresa = if ("codigoEmpresa" in campos)
                            R.string.auth_api_codigo_empresa_invalido else null,
                        errorRegistro = errorGeneral
                    )
                }
            } catch (e: IOException) {
                Log.e("PagoFlexAuth", "Error de conexión al registrar", e)
                _uiState.update {
                    it.copy(errorRegistro = R.string.auth_api_error_red)
                }
            } catch (e: Exception) {
                Log.e("PagoFlexAuth", "Error inesperado al registrar", e)
                _uiState.update {
                    it.copy(errorRegistro = R.string.auth_error_registro)
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
