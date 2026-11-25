package com.pagoflex.app.features.cobranza.data.remote

import androidx.annotation.StringRes
import com.pagoflex.app.R
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.core.data.remote.ErrorApi
import com.pagoflex.app.core.data.remote.llamar
import com.pagoflex.app.core.data.remote.llamarONulo
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.rules.MotivoAnulacion
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.core.validators.Validaciones
import com.pagoflex.app.features.cobranza.data.CompromisoRegistrado
import com.pagoflex.app.features.cobranza.data.PersonaEncontrada
import com.pagoflex.app.features.cobranza.data.ResultadoRegistro
import com.pagoflex.app.features.cobranza.presentation.model.AnulacionCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.CompromisoEmpresa
import com.pagoflex.app.features.cobranza.presentation.model.FormularioCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.PersonaCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.ReporteDelCompromiso
import com.pagoflex.app.features.reportes.data.remote.SoporteApi
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime

// cobranza contra el backend: mismas respuestas que la versión con room
class CobranzaRemoto(
    private val api: CobranzaApi,
    private val soporte: SoporteApi? = null,
) {

    companion object {
        fun crear(): CobranzaRemoto =
            CobranzaRemoto(
                RetrofitClient.retrofit.create(CobranzaApi::class.java),
                RetrofitClient.retrofit.create(SoporteApi::class.java)
            )
    }

    // reporte sin resolver del cobro; lo lleva soporte-service (RN-22)
    suspend fun reporteActivo(folio: String): ReporteDelCompromiso? {
        val reporte = llamarONulo { requireNotNull(soporte).reporteActivo(folio) } ?: return null
        return ReporteDelCompromiso(
            numero = reporte.numero,
            motivoRes = MotivoReporte.valueOf(reporte.motivo).labelRes,
            comentario = reporte.comentario,
            estadoRes = when (reporte.estado) {
                "ABIERTO" -> EstadoReporte.ABIERTO.labelRes
                "RESUELTO" -> EstadoReporte.RESUELTO.labelRes
                else -> EstadoReporte.EN_GESTION.labelRes
            },
            fecha = LocalDateTime.parse(reporte.fechaCreacion)
        )
    }

    // nombre de la empresa del ejecutivo o null si no tiene una asignada
    suspend fun nombreEmpresa(): String? = try {
        llamar { api.empresa() }.nombre
    } catch (e: ErrorApi) {
        if (e.codigo == 403 || e.codigo == 404) null else throw e
    }

    suspend fun obtenerCompromisos(): List<CompromisoEmpresa> =
        llamar { api.compromisos() }.map { it.aModelo() }

    suspend fun obtenerDetalle(folio: String): CompromisoEmpresa? =
        llamarONulo { api.detalle(folio) }?.aModelo()

    suspend fun buscarPersona(rut: String): PersonaEncontrada? =
        llamarONulo { api.persona(Validaciones.formatearRut(rut)) }?.let { PersonaEncontrada(it.nombre, it.correo) }

    // las validaciones de campos ya se hicieron en el repositorio
    suspend fun registrar(formulario: FormularioCompromiso, personaNueva: Boolean): ResultadoRegistro =
        try {
            val creado = llamar {
                api.registrar(
                    RegistrarCompromisoBody(
                        rut = Validaciones.formatearRut(formulario.rut),
                        nombres = formulario.nombres.trim().takeIf { personaNueva },
                        apellidos = formulario.apellidos.trim().takeIf { personaNueva },
                        correo = formulario.correo.trim().lowercase().takeIf { personaNueva },
                        telefono = formulario.telefono.trim().takeIf { personaNueva },
                        concepto = formulario.concepto.trim(),
                        monto = Validaciones.parsearMonto(formulario.monto)!!,
                        fechaVencimiento = Validaciones.parsearFecha(formulario.vencimiento)!!.toString(),
                        numeroCuota = formulario.numeroCuota.trim().toIntOrNull(),
                        totalCuotas = formulario.totalCuotas.trim().toIntOrNull(),
                    )
                )
            }
            ResultadoRegistro.Registrado(
                CompromisoRegistrado(creado.folio, creado.monto, LocalDate.parse(creado.fechaVencimiento))
            )
        } catch (e: ErrorApi) {
            ResultadoRegistro.ConErrores(errorGeneral = errorRegistro(e))
        } catch (_: IOException) {
            ResultadoRegistro.ConErrores(errorGeneral = R.string.api_error_conexion)
        }

    // devuelve el id del mensaje de error o null si se anuló
    suspend fun anular(folio: String, motivo: MotivoAnulacion, comentario: String): Int? =
        try {
            llamar { api.anular(folio, AnularCompromisoBody(motivo.name, comentario.trim().ifBlank { null })) }
            null
        } catch (e: ErrorApi) {
            errorAnulacion(e)
        } catch (_: IOException) {
            R.string.api_error_conexion
        }

    // ---------- helpers privados ----------

    @StringRes
    private fun errorRegistro(e: ErrorApi): Int = when (e.codigo) {
        409 -> R.string.cob_error_duplicado
        403 -> R.string.cob_error_sin_empresa
        else -> R.string.cob_error_generico
    }

    @StringRes
    private fun errorAnulacion(e: ErrorApi): Int = when {
        e.codigo == 404 -> R.string.cob_error_no_encontrado
        e.codigo == 409 && e.detalle?.contains("pagado") == true -> R.string.cob_error_anular_pagado
        e.codigo == 409 && e.detalle?.contains("anulado") == true -> R.string.cob_error_ya_anulado
        e.codigo == 403 -> R.string.cob_error_sin_empresa
        else -> R.string.cob_error_generico
    }

    private fun CompromisoDto.aModelo(): CompromisoEmpresa = CompromisoEmpresa(
        folio = folio,
        concepto = concepto,
        numeroCuota = numeroCuota,
        totalCuotas = totalCuotas,
        monto = monto,
        recargo = recargo,
        fechaVencimiento = LocalDate.parse(fechaVencimiento),
        estado = EstadoCompromiso.valueOf(estado),
        diasAtraso = diasAtraso,
        persona = PersonaCompromiso(persona.nombre, persona.rut, persona.correo.orEmpty(), persona.telefono),
        // los pagos todavía no están en el backend
        pago = null,
        registradoPor = registradoPor,
        fechaRegistro = LocalDateTime.parse(fechaRegistro),
        anulacion = anulacion?.let {
            AnulacionCompromiso(
                MotivoAnulacion.valueOf(it.motivo), it.comentario, LocalDateTime.parse(it.fecha), it.ejecutivo
            )
        }
    )
}
