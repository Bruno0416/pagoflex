package com.pagoflex.app.features.pago.data

import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.pago.data.remote.dto.request.ConfirmarPagoRequest
import com.pagoflex.app.features.pago.data.remote.dto.response.CotizacionPagoResponse
import com.pagoflex.app.features.pago.data.remote.PagoApi
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.create

/** Pago simulado mediante Cobranza Service. Ningún dato de tarjeta sale del dispositivo. */
class PagoRepository(
    private val api: PagoApi = RetrofitClient.retrofit.create<PagoApi>()
) {

    suspend fun prepararPago(folio: String): CotizacionPagoResponse = try {
        // 1. solicitar una cotizacion calculada por el backend
        api.cotizar(folio)
    } catch (e: HttpException) {
        // 1. traducir el rechazo de la cotizacion
        throw interpretarError(e)
    }

    suspend fun confirmarPago(
        resumen: CotizacionPagoResponse,
        metodo: MetodoPagoModel,
        idempotencyKey: String
    ): String {
        val respuesta = try {
            // 1. enviar medio de pago, cotizacion y llave de idempotencia
            api.pagar(
                folio = resumen.folio,
                idempotencyKey = idempotencyKey,
                body = ConfirmarPagoRequest(
                    medioPago = metodo.name,
                    cotizacionId = resumen.cotizacionId
                )
            )
        } catch (e: HttpException) {
            // 1. traducir el rechazo del pago
            throw interpretarError(e)
        }
        // 2. exigir el numero de comprobante de un pago confirmado
        return respuesta.numeroComprobante.takeIf { it.isNotBlank() }
            ?: error("El servidor no devolvió el número de comprobante")
    }

    /** Mapea el ProblemDetail real del backend sin duplicar sus modelos de presentación. */
    private fun interpretarError(error: HttpException): Exception {
        // 1. leer el error estructurado del backend
        val problema = runCatching {
            JSONObject(error.response()?.errorBody()?.string().orEmpty())
        }.getOrNull()
        // 2. recuperar la cuota previa que bloquea el pago
        val folioAnterior = problema?.optJSONObject("details")
            ?.optString("folioAnterior")?.takeIf(String::isNotBlank)
        // 3. identificar la regla de negocio rechazada
        val motivo = when (problema?.optString("code")) {
            "ALREADY_PAID" -> MotivoRechazoPago.YA_PAGADO
            "IN_REVIEW" -> MotivoRechazoPago.EN_REVISION
            "CANCELLED" -> MotivoRechazoPago.ANULADO
            "PAYMENT_OUT_OF_ORDER" -> MotivoRechazoPago.ORDEN
            "QUOTE_CHANGED", "CONCURRENT_UPDATE" -> MotivoRechazoPago.MONTO_CAMBIO
            "INVALID_PAYMENT_METHOD" -> MotivoRechazoPago.MEDIO_INVALIDO
            "INVALID_IDEMPOTENCY_KEY", "IDEMPOTENCY_KEY_REUSED" -> MotivoRechazoPago.NO_DISPONIBLE
            else -> if (error.code() == 403 || error.code() == 404) {
                MotivoRechazoPago.NO_DISPONIBLE
            } else null
        }
        // 4. conservar el error http si no hay un motivo reconocido
        return if (motivo != null) PagoRechazado(motivo, folioAnterior) else error
    }
}

enum class MotivoRechazoPago {
    NO_DISPONIBLE, EN_REVISION, ANULADO, YA_PAGADO, ORDEN, MONTO_CAMBIO, MEDIO_INVALIDO
}

class PagoRechazado(
    val motivo: MotivoRechazoPago,
    val folioAnterior: String? = null
) : Exception()
