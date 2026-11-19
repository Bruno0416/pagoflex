package com.pagoflex.app.features.cobranza.data.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

// api falsa: responde desde memoria para probar sin backend ni red
class FakeCobranzaApi : CobranzaApi {

    // compromisos de la empresa como los devolvería cobranza-service
    var compromisos = listOf(
        compromisoDto("CP-2026-0106", "EN_REVISION", monto = 310_000, recargo = 8_500),
        compromisoDto("CP-2026-0107", "PENDIENTE", monto = 310_000, recargo = 0),
    )

    // personas registradas por rut
    var personas = mapOf("12.476.318-5" to PersonaDto("Luis Alberto Muñoz Tapia", "lmunoz@correo.ejemplo"))

    // interruptores para simular respuestas del backend
    var sinConexion = false
    var codigoAlRegistrar = 201
    var detalleError = ""

    // lo último que recibió, para revisarlo en la prueba
    var ultimoRegistro: RegistrarCompromisoBody? = null

    override suspend fun empresa(): Response<EmpresaDto> = responder(EmpresaDto("EC-01", "Altos del Maipo"))

    override suspend fun compromisos(): Response<List<CompromisoDto>> = responder(compromisos)

    override suspend fun detalle(folio: String): Response<CompromisoDto> {
        val encontrado = compromisos.firstOrNull { it.folio == folio } ?: return error(404)
        return responder(encontrado)
    }

    override suspend fun persona(rut: String): Response<PersonaDto> {
        val encontrada = personas[rut] ?: return error(404)
        return responder(encontrada)
    }

    override suspend fun registrar(body: RegistrarCompromisoBody): Response<CompromisoDto> {
        if (sinConexion) throw IOException("Servidor apagado")
        ultimoRegistro = body
        if (codigoAlRegistrar != 201) return error(codigoAlRegistrar)
        return Response.success(
            compromisoDto("CP-2026-0001", "PENDIENTE", monto = body.monto, recargo = 0, vence = body.fechaVencimiento)
        )
    }

    override suspend fun anular(folio: String, body: AnularCompromisoBody): Response<CompromisoDto> = TODO()

    override suspend fun misCompromisos(): Response<List<MiCompromisoDto>> = responder(
        compromisos.map {
            MiCompromisoDto(it.folio, it.empresa, it.concepto, it.numeroCuota, it.totalCuotas, it.total,
                it.fechaVencimiento, it.estado)
        }
    )

    // ---------- helpers ----------

    private fun <T> responder(cuerpo: T): Response<T> {
        if (sinConexion) throw IOException("Servidor apagado")
        return Response.success(cuerpo)
    }

    // error con el mismo formato ProblemDetail que usa el backend
    private fun <T> error(codigo: Int): Response<T> =
        Response.error(codigo, """{"detail":"$detalleError"}""".toResponseBody("application/json".toMediaType()))

    companion object {
        fun compromisoDto(
            folio: String,
            estado: String,
            monto: Long,
            recargo: Long,
            vence: String = "2026-09-30",
        ) = CompromisoDto(
            folio = folio,
            empresa = "Altos del Maipo",
            concepto = "Cuota pie departamento",
            numeroCuota = 14,
            totalCuotas = 36,
            monto = monto,
            recargo = recargo,
            total = monto + recargo,
            fechaVencimiento = vence,
            estado = estado,
            diasAtraso = 0,
            persona = PersonaCompromisoDto("Luis Alberto Muñoz Tapia", "12476318-5", "lmunoz@correo.ejemplo", null),
            registradoPor = "psaavedra@altosmaipo.ejemplo",
            fechaRegistro = "2026-08-30T09:00:00",
            anulacion = null,
        )
    }
}
