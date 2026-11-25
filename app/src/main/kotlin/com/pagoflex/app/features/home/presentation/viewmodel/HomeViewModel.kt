package com.pagoflex.app.features.home.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.home.data.HomeRepository
import com.pagoflex.app.features.home.presentation.model.HomeUiState
import com.pagoflex.app.features.home.presentation.model.ResumenInicio
import com.pagoflex.app.features.home.data.remote.dto.response.CumplimientoHomeResponse
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

    fun cargarInicio() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            try {
                val fechaActual = LocalDate.now()
                val periodo = YearMonth.from(fechaActual)
                // 1. consultar resumen mensual y compromisos del usuario
                val (resumenApi, compromisos) = withContext(Dispatchers.IO) {
                    homeRepository.obtenerResumen(periodo) to homeRepository.obtenerCompromisos()
                }
                // 2. recuperar los datos opcionales sin bloquear el inicio
                val cabecera = try {
                    withContext(Dispatchers.IO) { homeRepository.obtenerCabecera() }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    null
                }
                val avatar = try {
                    withContext(Dispatchers.IO) { homeRepository.obtenerAvatar() }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    null
                }
                val cumplimiento = try {
                    withContext(Dispatchers.IO) { homeRepository.obtenerCumplimiento() }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    CumplimientoHomeResponse(0, 0, null, null)
                }

                // 3. filtrar compromisos del periodo y ordenar vencimientos
                val compromisosMes = compromisos.filter { compromiso ->
                    YearMonth.from(LocalDate.parse(compromiso.fechaVencimiento)) == periodo &&
                        compromiso.estado in setOf("PENDIENTE", "VENCIDO")
                }.sortedBy { it.fechaVencimiento }

                val pendientes = compromisos.filter { it.estado == "PENDIENTE" }
                    .sortedBy { it.fechaVencimiento }
                val vencidos = compromisos.filter { it.estado == "VENCIDO" }
                    .sortedBy { it.fechaVencimiento }

                // 4. construir el resumen con los datos disponibles
                val resumen = ResumenInicio(
                    totalPorPagar = resumenApi.porPagarMes,
                    totalRecargos = compromisosMes.sumOf { it.recargo },
                    cantidadCompromisos = compromisosMes.size,
                    cantidadOrganizaciones = compromisosMes.map { it.empresa }.distinct().size,
                    cantidadVencidos = resumenApi.vencidos,
                    proximoCompromiso = pendientes.firstOrNull(),
                    vencidoDestacado = vencidos.firstOrNull(),
                    cumplimiento = cumplimiento
                )

                val nombres = cabecera?.nombres.orEmpty().trim()
                val apellidos = cabecera?.apellidos.orEmpty().trim()
                // 5. publicar el contenido del inicio
                _uiState.update {
                    it.copy(
                        nombreUsuario = nombres.substringBefore(' '),
                        inicialesUsuario = (nombres.take(1) + apellidos.take(1)).uppercase(),
                        avatarUsuario = avatar,
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
            } catch (e: Exception) {
                // 5. mostrar un error si falla la consulta principal
                Log.e("PagoFlexHome", "No fue posible cargar el resumen", e)
                _uiState.update { it.copy(cargando = false, mensajeError = R.string.home_error_cargar) }
            }
        }
    }
}
