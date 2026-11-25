package com.pagoflex.app.features.auth.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.data.remote.dto.request.LoginRequest
import com.pagoflex.app.features.auth.presentation.model.LoginUiState
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun limpiarFormulario() {
        _uiState.value = LoginUiState()
    }

    fun setCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorLogin = null) }
    }

    fun validarCorreoField() {
        _uiState.update {
            it.copy(errorCorreo = AuthValidators.validarCorreo(it.correo))
        }
    }

    fun setContrasena(contrasena: String) {
        _uiState.update {
            it.copy(contrasena = contrasena, errorContrasena = null, errorLogin = null)
        }
    }

    fun validarContrasenaField() {
        _uiState.update {
            it.copy(errorContrasena = AuthValidators.validarContrasena(it.contrasena))
        }
    }

    fun iniciarSesion() {
        val estado = _uiState.value
        if (estado.cargando) return

        val correo = estado.correo.trim()
        val errorCorreo = AuthValidators.validarCorreo(correo)
        val errorContrasena = AuthValidators.validarContrasena(estado.contrasena)
        if (errorCorreo != null || errorContrasena != null) {
            _uiState.update {
                it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena)
            }
            return
        }

        _uiState.update { it.copy(cargando = true, errorLogin = null) }
        viewModelScope.launch {
            try {
                authRepository.iniciarSesion(
                    LoginRequest(correo = correo, contrasena = estado.contrasena)
                )
                limpiarFormulario()
            } catch (e: CancellationException) {
                throw e

            } catch (e: HttpException) {
                Log.e("PagoFlexAuth", "HTTP ${e.code()} en login", e)

                _uiState.update {
                    it.copy(
                        errorLogin = if (e.code() == 401)
                            R.string.auth_error_credenciales
                        else
                            R.string.auth_error_iniciar_sesion
                    )
                }
            } catch (e: IOException) {
                Log.e("PagoFlexAuth", "Error de conexión en login", e)

                _uiState.update {
                    it.copy(errorLogin = R.string.auth_api_error_red)
                }
            } catch (e: Exception) {
                Log.e("PagoFlexAuth", "Error inesperado en login", e)

                _uiState.update {
                    it.copy(errorLogin = R.string.auth_error_iniciar_sesion)
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
