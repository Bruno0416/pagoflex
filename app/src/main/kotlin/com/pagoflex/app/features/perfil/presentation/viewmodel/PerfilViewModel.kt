package com.pagoflex.app.features.perfil.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import com.pagoflex.app.features.perfil.data.PerfilRepository
import com.pagoflex.app.features.perfil.presentation.model.PerfilUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: PerfilRepository,
    private val usuarioId: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    init { cargarPerfil() }

    // ---------- consulta de perfil ----------

    fun cargarPerfil() {
        viewModelScope.launch {
            // 1. preparar el estado de carga
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            // 2. consultar al usuario activo
            try {
                val perfil = repository.obtenerPerfil(usuarioId)
                _uiState.update {
                    it.copy(
                        perfil = perfil,
                        nombres = perfil?.nombres.orEmpty(),
                        apellidos = perfil?.apellidos.orEmpty(),
                        correo = perfil?.correo.orEmpty(),
                        cargando = false,
                        errorGeneral = if (perfil == null) R.string.perfil_no_disponible else null
                    )
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) {
                _uiState.update { it.copy(cargando = false, errorGeneral = R.string.perfil_error_cargar) }
            }
        }
    }

    // ---------- edicion de datos ----------
    fun iniciarEdicion() {
        if (_uiState.value.perfil == null || _uiState.value.guardando) return
        _uiState.update { it.copy(editando = true, mensaje = null) }
    }

    fun cancelarEdicion() {
        if (_uiState.value.guardando) return
        _uiState.update { estado ->
            val original = estado.perfil
            estado.copy(
                editando = false,
                nombres = original?.nombres.orEmpty(),
                apellidos = original?.apellidos.orEmpty(),
                correo = original?.correo.orEmpty(),
                errorNombres = null, errorApellidos = null,
                errorCorreo = null, errorContrasena = null,
                errorGeneral = null
            )
        }
    }

    fun setNombres(valor: String) = _uiState.update { it.copy(nombres = valor, errorNombres = null) }
    fun setApellidos(valor: String) = _uiState.update { it.copy(apellidos = valor, errorApellidos = null) }
    fun setCorreo(valor: String) = _uiState.update { it.copy(correo = valor, errorCorreo = null, errorContrasena = null) }

    fun guardarCambios(contrasenaActual: String) {
        val estado = _uiState.value
        val original = estado.perfil ?: return
        if (!estado.editando || estado.guardando) return
        // 1. normalizar y validar los datos ingresados
        val nombres = AuthValidators.normalizarNombre(estado.nombres)
        val apellidos = AuthValidators.normalizarNombre(estado.apellidos)
        val correo = estado.correo.trim().lowercase()
        val errorNombres = AuthValidators.validarNombres(nombres)
        val errorApellidos = AuthValidators.validarApellidos(apellidos)
        val errorCorreo = AuthValidators.validarCorreo(correo)
        val errorContrasena = if (correo != original.correo && contrasenaActual.isBlank()) {
            R.string.perfil_contrasena_requerida
        } else null
        if (listOf(errorNombres, errorApellidos, errorCorreo, errorContrasena).any { it != null }) {
            _uiState.update {
                it.copy(
                    errorNombres = errorNombres, errorApellidos = errorApellidos,
                    errorCorreo = errorCorreo, errorContrasena = errorContrasena
                )
            }
            return
        }
        // 2. evitar escrituras cuando no hay cambios
        if (nombres == original.nombres && apellidos == original.apellidos && correo == original.correo) {
            cancelarEdicion()
            return
        }
        _uiState.update { it.copy(guardando = true, errorGeneral = null) }
        viewModelScope.launch {
            // 3. guardar los cambios y reflejar el resultado
            try {
                when (repository.actualizarDatos(
                    usuarioId, original.correo, nombres, apellidos, correo, contrasenaActual
                )) {
                    PerfilRepository.Resultado.GUARDADO -> _uiState.update {
                        it.copy(
                            perfil = original.copy(nombres = nombres, apellidos = apellidos, correo = correo),
                            nombres = nombres, apellidos = apellidos, correo = correo,
                            editando = false, mensaje = R.string.perfil_datos_guardados
                        )
                    }
                    PerfilRepository.Resultado.CORREO_OCUPADO -> _uiState.update {
                        it.copy(errorCorreo = R.string.perfil_correo_ocupado)
                    }
                    PerfilRepository.Resultado.CONTRASENA_INCORRECTA -> _uiState.update {
                        it.copy(errorContrasena = R.string.perfil_contrasena_incorrecta)
                    }
                    PerfilRepository.Resultado.SIN_USUARIO -> _uiState.update {
                        it.copy(errorGeneral = R.string.perfil_no_disponible)
                    }
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) {
                _uiState.update { it.copy(errorGeneral = R.string.perfil_error_guardar) }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }

    // ---------- fotografía ----------
    fun guardarAvatar(bitmap: Bitmap) = operarAvatar {
        val bytes = repository.guardarAvatar(usuarioId, bitmap)
        _uiState.update { it.copy(perfil = it.perfil?.copy(avatar = bytes), mensaje = R.string.perfil_foto_guardada) }
    }

    fun eliminarAvatar() = operarAvatar {
        repository.eliminarAvatar(usuarioId)
        _uiState.update { it.copy(perfil = it.perfil?.copy(avatar = null), mensaje = R.string.perfil_foto_eliminada) }
    }

    private fun operarAvatar(operacion: suspend () -> Unit) {
        if (_uiState.value.guardando || _uiState.value.editando || _uiState.value.perfil == null) return
        _uiState.update { it.copy(guardando = true, errorGeneral = null) }
        viewModelScope.launch {
            try { operacion() }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { _uiState.update { it.copy(errorGeneral = R.string.perfil_error_foto) } }
            finally { _uiState.update { it.copy(guardando = false) } }
        }
    }

    // ---------- limpieza de mensajes ----------
    fun limpiarErrorContrasena() = _uiState.update { it.copy(errorContrasena = null) }
    fun limpiarMensaje() = _uiState.update { it.copy(mensaje = null) }
}
