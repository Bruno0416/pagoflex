package com.pagoflex.app.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.cumplimiento.data.CumplimientoRepository
import com.pagoflex.app.features.home.data.HomeRepository
import com.pagoflex.app.features.reconocimientos.data.ReconocimientosRepository
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

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val cumplimientoRepository: CumplimientoRepository,
    private val reconocimientosRepository: ReconocimientosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(cargando = true))
    val uiState = _uiState.asStateFlow()

    // ---------- cargar datos del usuario ----------
    fun cargarInicio(usuarioId: Int) {
        if (usuarioId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            try {
                // 1. consultar datos en room
                val fechaActual = LocalDate.now()
                val (cabecera, compromisos) = withContext(Dispatchers.IO) {
                    homeRepository.obtenerCabecera(usuarioId) to
                            homeRepository.obtenerCompromisos(usuarioId, fechaActual)
                }
                val cumplimiento = withContext(Dispatchers.IO) {
                    cumplimientoRepository.obtenerCumplimiento(usuarioId, fechaActual)
                }

                if (cabecera == null) {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = R.string.home_error_usuario_no_encontrado
                        )
                    }
                    return@launch
                }

                // 2. filtrar compromisos del mes
                val periodo = YearMonth.from(fechaActual)
                val compromisosMes = compromisos.filter { compromiso ->
                    YearMonth.from(compromiso.fechaVencimiento) == periodo &&
                            compromiso.estado in listOf(
                        EstadoCompromiso.PENDIENTE,
                        EstadoCompromiso.VENCIDO
                    )
                }.sortedBy { it.fechaVencimiento }

                val pendientes = compromisos.filter {
                    it.estado == EstadoCompromiso.PENDIENTE
                }.sortedBy { it.fechaVencimiento }
                val vencidos = compromisos.filter {
                    it.estado == EstadoCompromiso.VENCIDO
                }.sortedBy { it.fechaVencimiento }

                // 3. calcular resumen
                val resumen = ResumenInicio(
                    totalPorPagar = compromisosMes.sumOf { it.total },
                    totalRecargos = compromisosMes.sumOf { it.recargo },
                    cantidadCompromisos = compromisosMes.size,
                    cantidadOrganizaciones = compromisosMes.map { it.organizacion }.distinct().size,
                    cantidadVencidos = vencidos.size,
                    proximoCompromiso = pendientes.firstOrNull(),
                    vencidoDestacado = vencidos.firstOrNull(),
                    cumplimiento = cumplimiento
                )

                // 4. actualizar la pantalla
                _uiState.update {
                    it.copy(
                        nombreUsuario = cabecera.nombres.trim().substringBefore(" "),
                        inicialesUsuario = (cabecera.nombres.trim().take(1) + cabecera.apellidos.trim().take(1)).uppercase(),
                        avatarUsuario = cabecera.avatar,
                        periodo = periodo,
                        resumen = resumen,
                        compromisos = compromisosMes,
                        ultimaActualizacion = LocalDateTime.now(),
                        cargando = false,
                        mensajeError = null
                    )
                }

                // 5. recuperar reconocimientos sin retrasar el resumen
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        reconocimientosRepository.evaluar(usuarioId)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        // el resumen no depende de los reconocimientos
                    }
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
