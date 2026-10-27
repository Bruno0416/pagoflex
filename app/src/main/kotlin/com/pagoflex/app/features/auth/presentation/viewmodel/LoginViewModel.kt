package com.pagoflex.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.core.profiles.model.SesionUsuario
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.presentation.model.LoginUiState
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun limpiarFormulario() {
        _uiState.value = LoginUiState()
    }

    // --- correo ---
    fun setCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorLogin = null) }
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
                errorLogin = null
            )
        }
    }

    fun validarContrasenaField() {
        val error = AuthValidators.validarContrasena(uiState.value.contrasena)

        _uiState.update { it.copy(errorContrasena = error) }
    }


    fun iniciarSesion(crearSesion: (SesionUsuario) -> Unit) {
        // 1. congelar estado
        val estado = uiState.value
        val correo = estado.correo.trim()
        val contrasena = estado.contrasena

        // 2. cancelar si esta cargando
        if (estado.cargando) return

        // 3. validar campos
        val errorCorreo = AuthValidators.validarCorreo(correo)
        val errorContrasena = AuthValidators.validarContrasena(contrasena)

        // 4. si alguno de los campos es inválido, mostrar estado y cancelar operación
        if (errorCorreo != null || errorContrasena != null) {
            _uiState.update {
                it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena)
            }
            return
        }

        // 5. comprobar credenciales

        // mostrar estado de carga
        _uiState.update { it.copy(cargando = true, errorLogin = null) }

        // iniciar corrutina para la operación de login
        viewModelScope.launch {
            try {
                // intentar iniciar sesión
                val sesion = authRepository.iniciarSesion(
                    correo,
                    contrasena
                )

                if (sesion != null) {
                    // Limpiar credenciales en memoria antes de entrar a la aplicación.
                    limpiarFormulario()
                    crearSesion(sesion)
                } else {
                    _uiState.update {
                        it.copy(errorLogin = R.string.auth_error_credenciales, cargando = false)
                    }
                }
            } catch (e: CancellationException) {
                // cancelar operación si se cancela la corrutina
                throw e
            } catch (_: Exception) {
                // sí hay algún error mostrar mensaje genérico
                _uiState.update {
                    it.copy(errorLogin = R.string.auth_error_iniciar_sesion, cargando = false)
                }
            } finally {
                // por último ocultar estado de carga al finalizar la operación
                _uiState.update { it.copy(cargando = false) }
            }

        }
    }
}