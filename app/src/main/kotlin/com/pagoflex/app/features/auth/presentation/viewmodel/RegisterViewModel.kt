package com.pagoflex.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.presentation.model.RegisterUiState
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(preguntas = authRepository.obtenerPreguntas()) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorRegistro = R.string.auth_error_registro) }
            }
        }
    }

    // --- rut ---
    fun setRut(rut: String) {
        _uiState.update { it.copy(rut = rut, errorRut = null, errorRegistro = null) }
    }

    fun validarRutField() {
        val error = AuthValidators.validarRut(uiState.value.rut)

        _uiState.update { it.copy(errorRut = error) }
    }

    // --- nombres ---
    fun setNombres(nombres: String) {
        _uiState.update { it.copy(nombres = nombres, errorNombres = null, errorRegistro = null) }
    }

    fun validarNombresField() {
        val error = AuthValidators.validarNombres(uiState.value.nombres)
        _uiState.update { it.copy(errorNombres = error) }
    }


    // --- apellidos ---
    fun setApellidos(apellidos: String) {
        _uiState.update {
            it.copy(
                apellidos = apellidos,
                errorApellidos = null,
                errorRegistro = null
            )
        }
    }

    fun validarApellidosField() {
        val error = AuthValidators.validarApellidos(uiState.value.apellidos)
        _uiState.update { it.copy(errorApellidos = error) }
    }

    // --- correo ---
    fun setCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorRegistro = null) }
    }

    fun validarCorreoField() {
        val error = AuthValidators.validarCorreo(uiState.value.correo)

        _uiState.update { it.copy(errorCorreo = error) }
    }


    // --- contraseña ---
    fun setContrasena(contrasena: String) {
        _uiState.update {
            it.copy(
                contrasena = contrasena,
                errorContrasena = null,
                errorRegistro = null
            )
        }
    }

    fun validarContrasenaField() {
        val error = AuthValidators.validarContrasena(uiState.value.contrasena)

        _uiState.update { it.copy(errorContrasena = error) }
    }

    // --- confirmar contraseña ---
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
        val error = AuthValidators.validarConfirmarContrasena(
            uiState.value.contrasena,
            uiState.value.confirmarContrasena
        )

        _uiState.update { it.copy(errorConfirmarContrasena = error, errorRegistro = null) }
    }

    // --- terminos y condiciones ---
    fun setPreguntaRecuperacion(idPregunta: Int) {
        _uiState.update {
            it.copy(
                idPreguntaRecuperacion = idPregunta,
                errorPreguntaRecuperacion = null,
                errorRegistro = null
            )
        }
    }

    fun setRespuestaRecuperacion(respuesta: String) {
        _uiState.update {
            it.copy(
                respuestaRecuperacion = respuesta,
                errorRespuestaRecuperacion = null,
                errorRegistro = null
            )
        }
    }

    fun validarRespuestaRecuperacionField() {
        val error = AuthValidators.validarRespuestaRecuperacion(uiState.value.respuestaRecuperacion)
        _uiState.update { it.copy(errorRespuestaRecuperacion = error) }
    }

    // --- terminos y condiciones ---
    fun setTerminosYCondiciones(value: Boolean) {
        _uiState.update {
            it.copy(
                terminosYCondiciones = value,
                errorTerminosYCondiciones = null,
                errorRegistro = null
            )
        }
    }

    // --- registro --
    fun registrar(onExito: () -> Unit) {
        // 1. congelar estado
        val estado = uiState.value
        val rut = estado.rut.trim()
        val nombres = AuthValidators.normalizarNombre(estado.nombres)
        val apellidos = AuthValidators.normalizarNombre(estado.apellidos)
        val correo = estado.correo.trim()
        val contrasena = estado.contrasena
        val confirmarContrasena = estado.confirmarContrasena
        val idPreguntaRecuperacion = estado.idPreguntaRecuperacion
        val respuestaRecuperacion = estado.respuestaRecuperacion

        val terminosYCondiciones = estado.terminosYCondiciones

        // 2. cancelar si esta cargando
        if (estado.cargando) return

        // 3 validar campos
        val errorRut = AuthValidators.validarRut(rut)
        val errorNombres = AuthValidators.validarNombres(nombres)
        val errorApellidos = AuthValidators.validarApellidos(apellidos)
        val errorCorreo = AuthValidators.validarCorreo(correo)
        val errorContrasena = AuthValidators.validarContrasena(contrasena)
        val errorConfirmarContrasena =
            AuthValidators.validarConfirmarContrasena(contrasena, confirmarContrasena)
        val errorPreguntaRecuperacion =
            AuthValidators.validarPreguntaRecuperacion(idPreguntaRecuperacion)
        val errorRespuestaRecuperacion =
            AuthValidators.validarRespuestaRecuperacion(respuestaRecuperacion)

        // 4. si alguno de los campos es inválido, mostrar estado y cancelar operación
        if (errorRut != null || errorNombres != null || errorApellidos != null || errorCorreo != null || errorContrasena != null || errorConfirmarContrasena != null || errorPreguntaRecuperacion != null || errorRespuestaRecuperacion != null || !terminosYCondiciones) {
            _uiState.update {
                it.copy(
                    errorRut = errorRut,
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorCorreo = errorCorreo,
                    errorContrasena = errorContrasena,
                    errorConfirmarContrasena = errorConfirmarContrasena,
                    errorPreguntaRecuperacion = errorPreguntaRecuperacion,
                    errorRespuestaRecuperacion = errorRespuestaRecuperacion,
                    errorTerminosYCondiciones = if (terminosYCondiciones) null else R.string.auth_error_terminos_y_condiciones
                )
            }
            return
        }

        // 5. registrar usuario si no hay error

        // mostrar estado de carga
        _uiState.update { it.copy(cargando = true, errorRegistro = null) }

        viewModelScope.launch {
            try {
                // el repositorio se encarga de hashear la contrasena
                authRepository.registrarUsuario(
                    AuthValidators.normalizarRut(rut),
                    nombres,
                    apellidos,
                    correo.lowercase(),
                    contrasena,
                    requireNotNull(idPreguntaRecuperacion),
                    respuestaRecuperacion
                )

                // 6. si no hubo errores se usa el callback de registro exitoso
                onExito()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(errorRegistro = R.string.auth_error_registro)
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
