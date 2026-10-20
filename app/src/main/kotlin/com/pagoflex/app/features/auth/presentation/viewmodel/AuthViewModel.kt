package com.pagoflex.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.core.profiles.SesionPreferences
import com.pagoflex.app.core.profiles.model.SesionUsuario
import com.pagoflex.app.features.auth.data.AuthRepository
import com.pagoflex.app.features.auth.presentation.model.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sesionPreferences: SesionPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        obtenerSesion()
    }

    private fun obtenerSesion() {
        viewModelScope.launch {
            try {
                // 1. obtener id almacenando
                val usuarioId = sesionPreferences.leer()

                // 2. validar que siga activo
                val sesion = if (usuarioId > 0) {
                    authRepository.obtenerSesion(usuarioId)
                } else {
                    null
                }

                // 3. eliminar sesion inválida
                if (usuarioId > 0 && sesion == null) {
                    sesionPreferences.borrar()
                }

                // 4. actualizar estado con la sesion válida
                _uiState.update {
                    it.copy(
                        cargando = false,
                        sesion = sesion

                    )
                }

            } catch (e: CancellationException) {
                // cancelar operación si se cancela la corrutina
                throw e
            } catch (e: Exception) {
                // sí hay algún error mostrar mensaje genérico
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = R.string.auth_error_restaurar_sesion
                    )
                }
            }
        }
    }

    fun crearSesion(sesion: SesionUsuario) {
        sesionPreferences.guardar(sesion.usuarioId)

        _uiState.update {
            it.copy(
                cargando = false,
                sesion = sesion
            )
        }
    }

    fun cerrarSesion() {
        sesionPreferences.borrar()

        _uiState.update {
            it.copy(
                cargando = false,
                sesion = null,
                error = null
            )
        }
    }
}