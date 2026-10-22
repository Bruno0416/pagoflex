package com.pagoflex.app.features.cobranza.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.core.validators.Validaciones
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.data.ResultadoRegistro
import com.pagoflex.app.features.cobranza.presentation.model.CampoCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.FormularioCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.RegistrarCompromisoUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RegistrarCompromisoViewModel(
    private val cobranzaRepository: CobranzaRepository,
    private val ahora: () -> LocalDateTime = LocalDateTime::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrarCompromisoUiState())
    val uiState: StateFlow<RegistrarCompromisoUiState> = _uiState.asStateFlow()

    private val camposPersona = setOf(
        CampoCompromiso.NOMBRES,
        CampoCompromiso.APELLIDOS,
        CampoCompromiso.CORREO,
        CampoCompromiso.TELEFONO,
    )

    // --- campos ---
    fun setCampo(campo: CampoCompromiso, valor: String) {
        _uiState.update { estado ->
            val f = estado.formulario
            val nuevo = when (campo) {
                CampoCompromiso.RUT -> f.copy(rut = valor)
                CampoCompromiso.NOMBRES -> f.copy(nombres = valor)
                CampoCompromiso.APELLIDOS -> f.copy(apellidos = valor)
                CampoCompromiso.CORREO -> f.copy(correo = valor)
                CampoCompromiso.TELEFONO -> f.copy(telefono = valor)
                CampoCompromiso.CONCEPTO -> f.copy(concepto = valor)
                CampoCompromiso.MONTO -> f.copy(monto = valor)
                CampoCompromiso.VENCIMIENTO -> f.copy(vencimiento = valor)
                CampoCompromiso.CUOTA -> f.copy(numeroCuota = valor)
            }
            estado.copy(formulario = nuevo, errores = estado.errores - campo, errorGeneral = null)
        }

        if (campo == CampoCompromiso.RUT) buscarPersona(valor)
    }

    // el total de cuotas comparte error con la cuota
    fun setTotalCuotas(valor: String) {
        _uiState.update {
            it.copy(
                formulario = it.formulario.copy(totalCuotas = valor),
                errores = it.errores - CampoCompromiso.CUOTA
            )
        }
    }

    // --- persona ---
    private fun buscarPersona(rut: String) {
        // 1. con rut inválido no se busca ni se piden datos
        val rutValido = Validaciones.validarRut(rut) == null
        if (!rutValido) {
            _uiState.update { it.copy(rutValido = false, personaEncontrada = null) }
            return
        }

        viewModelScope.launch {
            try {
                // 2. buscar persona y limpiar errores de datos personales
                val persona = cobranzaRepository.buscarPersona(rut)
                _uiState.update {
                    it.copy(rutValido = true, personaEncontrada = persona, errores = it.errores - camposPersona)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(rutValido = true, personaEncontrada = null) }
            }
        }
    }

    fun registrar(usuarioId: Int) {
        // 1. congelar estado y cancelar si está cargando
        val estado = uiState.value
        if (estado.cargando) return

        _uiState.update { it.copy(cargando = true, errorGeneral = null) }

        viewModelScope.launch {
            try {
                // 2. obtener ejecutivo y registrar con las reglas de RN-20
                val ejecutivo = cobranzaRepository.obtenerEjecutivo(usuarioId)
                if (ejecutivo == null) {
                    _uiState.update { it.copy(errorGeneral = R.string.cob_error_sin_empresa) }
                    return@launch
                }

                when (val resultado = cobranzaRepository.registrar(estado.formulario, ejecutivo, ahora())) {
                    is ResultadoRegistro.Registrado ->
                        _uiState.update { it.copy(registrado = resultado.compromiso, errores = emptyMap()) }

                    is ResultadoRegistro.ConErrores ->
                        _uiState.update {
                            it.copy(errores = resultado.errores, errorGeneral = resultado.errorGeneral)
                        }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorGeneral = R.string.cob_error_generico) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    // limpiar para registrar otro
    fun nuevoRegistro() {
        _uiState.value = RegistrarCompromisoUiState(formulario = FormularioCompromiso())
    }
}
