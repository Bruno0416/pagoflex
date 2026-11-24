package com.pagoflex.app.features.perfil.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import com.pagoflex.app.features.perfil.data.PerfilRepository
import com.pagoflex.app.features.perfil.data.remote.dto.request.ActualizarPerfilRequest
import com.pagoflex.app.features.perfil.presentation.model.PerfilUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PerfilViewModel(
    private val repository: PerfilRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    init { cargarPerfil() }

    // ---------- consulta de perfil ----------
    fun cargarPerfil() {
        viewModelScope.launch {
            // 1. consultar los datos del usuario autenticado
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            try {
                val perfil = repository.obtenerPerfil()
                // 2. recuperar el avatar sin bloquear el formulario si falla
                val avatar = if (perfil.urlAvatar.isNullOrBlank()) null else try {
                    repository.obtenerAvatar()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    null
                }
                // 3. reflejar los datos reales del servidor
                _uiState.update {
                    it.copy(
                        perfil = perfil,
                        avatar = avatar,
                        nombres = perfil.nombres,
                        apellidos = perfil.apellidos,
                        correo = perfil.correo,
                        cargando = false,
                        editando = false,
                        errorGeneral = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
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
            _uiState.update { it.copy(
                errorNombres = errorNombres, errorApellidos = errorApellidos,
                errorCorreo = errorCorreo, errorContrasena = errorContrasena
            ) }
            return
        }
        // 2. evitar peticiones si no hay cambios
        if (nombres == original.nombres && apellidos == original.apellidos && correo == original.correo) {
            cancelarEdicion()
            return
        }
        _uiState.update { it.copy(guardando = true, errorGeneral = null) }
        viewModelScope.launch {
            try {
                // 3. enviar los cambios y renovar la sesion si corresponde
                val perfil = repository.actualizarDatos(ActualizarPerfilRequest(
                    nombres = nombres,
                    apellidos = apellidos,
                    correo = correo,
                    contrasenaActual = contrasenaActual.takeIf { correo != original.correo }
                ))
                _uiState.update { it.copy(
                    perfil = perfil,
                    nombres = perfil.nombres,
                    apellidos = perfil.apellidos,
                    correo = perfil.correo,
                    editando = false,
                    mensaje = R.string.perfil_datos_guardados,
                    errorGeneral = null
                ) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                // 4. asignar errores del servidor a los campos existentes
                val campos = repository.camposInvalidos(e)
                _uiState.update { it.copy(
                    errorNombres = if ("nombres" in campos) R.string.perfil_error_guardar else null,
                    errorApellidos = if ("apellidos" in campos) R.string.perfil_error_guardar else null,
                    errorCorreo = when {
                        e.code() == 409 -> R.string.perfil_correo_ocupado
                        "correo" in campos -> R.string.perfil_error_guardar
                        else -> null
                    },
                    errorContrasena = if ("contrasenaActual" in campos) R.string.perfil_contrasena_incorrecta else null,
                    errorGeneral = if (campos.isEmpty() && e.code() != 409) R.string.perfil_error_guardar else null
                ) }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorGeneral = R.string.perfil_error_guardar) }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }

    // ---------- fotografia ----------
    fun guardarAvatar(bitmap: Bitmap) = operarAvatar {
        val (perfil, bytes) = repository.guardarAvatar(bitmap)
        _uiState.update { it.copy(perfil = perfil, avatar = bytes, mensaje = R.string.perfil_foto_guardada) }
    }

    fun eliminarAvatar() = operarAvatar {
        repository.eliminarAvatar()
        _uiState.update { estado ->
            estado.copy(perfil = estado.perfil?.copy(urlAvatar = null), avatar = null, mensaje = R.string.perfil_foto_eliminada)
        }
    }

    private fun operarAvatar(operacion: suspend () -> Unit) {
        if (_uiState.value.guardando || _uiState.value.editando || _uiState.value.perfil == null) return
        _uiState.update { it.copy(guardando = true, errorGeneral = null) }
        viewModelScope.launch {
            try {
                // 1. guardar el cambio de fotografia en el servidor
                operacion()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorGeneral = R.string.perfil_error_foto) }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }

    // ---------- limpieza de mensajes ----------
    fun limpiarErrorContrasena() = _uiState.update { it.copy(errorContrasena = null) }
    fun limpiarMensaje() = _uiState.update { it.copy(mensaje = null) }
}
