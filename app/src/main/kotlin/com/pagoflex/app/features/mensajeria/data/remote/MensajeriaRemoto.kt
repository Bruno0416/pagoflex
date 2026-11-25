package com.pagoflex.app.features.mensajeria.data.remote

import com.google.gson.Gson
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.core.data.remote.ErrorApi
import com.pagoflex.app.core.data.remote.llamar
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSource
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

// lo que llega por el canal en vivo de la conversación
sealed class EventoSse {
    // el backend confirmó la conexión (event:ready)
    data object Conectado : EventoSse()

    data class Mensaje(val mensaje: MensajeDto) : EventoSse()

    // se cortó y se va a reintentar
    data object Reconectando : EventoSse()

    // el backend la rechazó (401, 403, 404, 409): no se reintenta
    data class Rechazado(val codigo: Int) : EventoSse()
}

// conversación de un reporte contra el backend
class MensajeriaRemoto(
    private val api: MensajeriaApi,
    private val stream: Call.Factory,
    private val baseUrl: String,
) {

    companion object {
        private const val ESPERA_INICIAL = 1_000L
        private const val ESPERA_MAXIMA = 15_000L

        // mismo cliente autenticado, sin límite de lectura para el stream
        fun crear(): MensajeriaRemoto {
            val retrofit = RetrofitClient.retrofit
            val cliente = (retrofit.callFactory() as OkHttpClient).newBuilder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()
            return MensajeriaRemoto(retrofit.create(MensajeriaApi::class.java), cliente, retrofit.baseUrl().toString())
        }
    }

    private val gson = Gson()

    suspend fun abrir(numero: String): ConversacionDto = llamar { api.conversacion(numero) }

    suspend fun historial(numero: String, cursor: Long? = null): PaginaMensajesDto =
        llamar { api.mensajes(numero, cursor) }

    // el id lo genera la app para que reintentar no duplique el mensaje
    suspend fun enviar(numero: String, texto: String, clientMessageId: String = UUID.randomUUID().toString()): MensajeDto =
        llamar { api.enviar(numero, EnviarMensajeBody(texto, clientMessageId)) }

    suspend fun marcarLeido(numero: String, ultimaSecuencia: Long) {
        llamar { api.marcarLeido(numero, LecturaBody(ultimaSecuencia)) }
    }

    // mensajes en vivo; si se corta reconecta pidiendo lo que se perdió, hasta que cancelen la corrutina
    fun eventos(numero: String, ultimaSecuencia: Long): Flow<EventoSse> = channelFlow {
        var ultima = ultimaSecuencia
        var espera = ESPERA_INICIAL

        while (isActive) {
            val llamada = stream.newCall(peticionEventos(numero, ultima))

            // 1. la lectura bloquea el hilo: al cancelar la corrutina se corta la llamada
            val vigilante = launch {
                try {
                    awaitCancellation()
                } finally {
                    llamada.cancel()
                }
            }

            try {
                // 2. leer el stream línea por línea en un hilo de red
                withContext(Dispatchers.IO) {
                    llamada.execute().use { respuesta ->
                        if (!respuesta.isSuccessful) throw ErrorApi(respuesta.code, null)
                        val fuente = respuesta.body?.source() ?: throw IOException("sin cuerpo")
                        leerEventos(fuente) { tipo, datos ->
                            when (tipo) {
                                "ready" -> {
                                    espera = ESPERA_INICIAL
                                    send(EventoSse.Conectado)
                                }

                                "message" -> {
                                    val mensaje = gson.fromJson(datos, MensajeDto::class.java)
                                    if (mensaje.secuencia > ultima) ultima = mensaje.secuencia
                                    send(EventoSse.Mensaje(mensaje))
                                }
                            }
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ErrorApi) {
                // 3. si el backend la rechaza no tiene sentido reintentar
                if (e.codigo in 400..499 && e.codigo != 408 && e.codigo != 429) {
                    send(EventoSse.Rechazado(e.codigo))
                    return@channelFlow
                }
            } catch (_: IOException) {
                // corte de red o el backend cerró a los 30 min: se reintenta
            } catch (_: RuntimeException) {
                // un evento mal formado no debe botar el chat
            } finally {
                vigilante.cancel()
            }

            // 4. avisar y esperar cada vez más antes de reconectar (1s, 2s, 4s… máx 15s)
            send(EventoSse.Reconectando)
            delay(espera)
            espera = (espera * 2).coerceAtMost(ESPERA_MAXIMA)
        }
    }

    private fun peticionEventos(numero: String, ultima: Long): Request =
        Request.Builder()
            .url(baseUrl.trimEnd('/') + "/conversaciones/reportes/$numero/eventos")
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")
            // el backend reenvía lo que llegó después de esta secuencia
            .header("Last-Event-ID", ultima.toString())
            .build()

    // formato sse: líneas "campo:valor" y una línea vacía cierra cada evento
    private suspend fun leerEventos(fuente: BufferedSource, alEvento: suspend (tipo: String, datos: String) -> Unit) {
        var tipo = "message"
        val datos = StringBuilder()

        while (true) {
            val linea = fuente.readUtf8Line() ?: return
            when {
                linea.isEmpty() -> {
                    if (datos.isNotEmpty()) alEvento(tipo, datos.toString())
                    tipo = "message"
                    datos.clear()
                }

                // comentario o latido del servidor
                linea.startsWith(":") -> Unit

                else -> {
                    val separador = linea.indexOf(':')
                    val campo = if (separador < 0) linea else linea.substring(0, separador)
                    val valor = if (separador < 0) "" else linea.substring(separador + 1).removePrefix(" ")
                    when (campo) {
                        "event" -> tipo = valor
                        "data" -> {
                            if (datos.isNotEmpty()) datos.append('\n')
                            datos.append(valor)
                        }
                    }
                }
            }
        }
    }
}
