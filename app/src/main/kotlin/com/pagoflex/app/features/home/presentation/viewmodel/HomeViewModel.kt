package com.pagoflex.app.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.home.data.HomeRepository
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.home.presentation.model.HomeUiState
import com.pagoflex.app.features.home.presentation.model.ResumenInicio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(cargando = true))
    val uiState = _uiState.asStateFlow()

    // ---------- cargar datos del usuario ----------
    fun cargarInicio(usuarioId: Int) {
        if (usuarioId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            try {
                // 1. recuperar los datos actuales desde room
                val fechaActual = LocalDate.now()
                val (nombre, compromisos) = withContext(Dispatchers.IO) {
                    homeRepository.obtenerNombre(usuarioId) to
                            homeRepository.obtenerCompromisos(usuarioId, fechaActual)
                }

                if (nombre == null) {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = R.string.home_error_usuario_no_encontrado
                        )
                    }
                    return@launch
                }

                // 2. filtrar compromisos por pagar en el mes actual
                val periodo = YearMonth.from(fechaActual)
                val compromisosMes = compromisos.filter { compromiso ->
                    YearMonth.from(compromiso.fechaVencimiento) == periodo &&
                            compromiso.estado in listOf(
                        EstadoCompromiso.PENDIENTE,
                        EstadoCompromiso.VENCIDO
                    )
                }.sortedBy { it.fechaVencimiento }

                val pendientes = compromisosMes.filter {
                    it.estado == EstadoCompromiso.PENDIENTE
                }
                val vencidos = compromisosMes.filter {
                    it.estado == EstadoCompromiso.VENCIDO
                }

                // 3. calcular el resumen con los registros recuperados
                val resumen = ResumenInicio(
                    totalPorPagar = compromisosMes.sumOf { it.total },
                    totalRecargos = compromisosMes.sumOf { it.recargo },
                    cantidadCompromisos = compromisosMes.size,
                    cantidadOrganizaciones = compromisosMes.map { it.organizacion }.distinct().size,
                    cantidadVencidos = vencidos.size,
                    proximoCompromiso = pendientes.firstOrNull(),
                    vencidoDestacado = vencidos.firstOrNull(),
                    porcentajeCumplimiento = null // el resumen de 12 meses no está completo en la db
                )

                // 4. actualizar la pantalla
                _uiState.update {
                    it.copy(
                        nombreUsuario = nombre,
                        periodo = periodo,
                        resumen = resumen,
                        compromisos = compromisosMes,
                        ultimaActualizacion = LocalDateTime.now(),
                        cargando = false,
                        mensajeError = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(cargando = false, mensajeError = R.string.home_error_cargar)
                }
            }
        }
    }
}
