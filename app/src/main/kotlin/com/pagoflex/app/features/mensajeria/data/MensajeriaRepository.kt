package com.pagoflex.app.features.mensajeria.data

import androidx.annotation.StringRes
import com.pagoflex.app.R
import com.pagoflex.app.core.data.remote.ErrorApi
import com.pagoflex.app.features.mensajeria.data.remote.EventoSse
import com.pagoflex.app.features.mensajeria.data.remote.MensajeDto
import com.pagoflex.app.features.mensajeria.data.remote.MensajeriaRemoto
import com.pagoflex.app.features.mensajeria.presentation.model.Conversacion
import com.pagoflex.app.features.mensajeria.presentation.model.MensajeChat
import com.pagoflex.app.features.mensajeria.presentation.model.PaginaChat
import com.pagoflex.app.features.mensajeria.presentation.model.RolChat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDateTime

// lo que llega en vivo ya convertido para la pantalla
sealed class EventoChat {
    data object Conectado : EventoChat()
    data object Reconectando : EventoChat()
    data class Nuevo(val mensaje: MensajeChat) : EventoChat()
    data class Rechazado(@StringRes val mensaje: Int) : EventoChat()
}

// mensajería del reporte entre el agente y el ejecutivo; quién participa lo decide el backend
class MensajeriaRepository(
    private val remoto: MensajeriaRemoto,
    private val rolPropio: RolChat,
) {

    suspend fun abrir(numero: String): Conversacion {
        val conversacion = remoto.abrir(numero)
        return Conversacion(
            numero = conversacion.reporteNumero,
            soloLectura = conversacion.soloLectura,
            ultimaSecuencia = conversacion.ultimaSecuencia,
            ultimaSecuenciaLeida = conversacion.ultimaSecuenciaLeida
        )
    }

    suspend fun historial(numero: String, cursor: Long? = null): PaginaChat {
        val pagina = remoto.historial(numero, cursor)
        return PaginaChat(pagina.mensajes.map { it.aModelo() }, pagina.cursorAnterior)
    }

    suspend fun enviar(numero: String, texto: String, clientMessageId: String): MensajeChat =
        remoto.enviar(numero, texto.trim(), clientMessageId).aModelo()

    suspend fun marcarLeido(numero: String, ultimaSecuencia: Long) =
        remoto.marcarLeido(numero, ultimaSecuencia)

    fun eventos(numero: String, ultimaSecuencia: Long): Flow<EventoChat> =
        remoto.eventos(numero, ultimaSecuencia).map { evento ->
            when (evento) {
                EventoSse.Conectado -> EventoChat.Conectado
                EventoSse.Reconectando -> EventoChat.Reconectando
                is EventoSse.Mensaje -> EventoChat.Nuevo(evento.mensaje.aModelo())
                is EventoSse.Rechazado -> EventoChat.Rechazado(mensajeAlAbrir(evento.codigo))
            }
        }

    // ---------- mensajes de error amigables ----------

    // al abrir o leer: 409 es que ningún agente lo tomó todavía
    @StringRes
    fun errorAlAbrir(e: Exception): Int = when (e) {
        is ErrorApi -> mensajeAlAbrir(e.codigo)
        is IOException -> R.string.api_error_conexion
        else -> R.string.chat_error_generico
    }

    // al escribir: 409 es que el reporte quedó resuelto
    @StringRes
    fun errorAlEnviar(e: Exception): Int = when {
        e is ErrorApi && e.codigo == 409 -> R.string.chat_solo_lectura
        e is ErrorApi && e.codigo == 400 -> R.string.chat_error_texto
        e is ErrorApi -> mensajeAlAbrir(e.codigo)
        e is IOException -> R.string.api_error_conexion
        else -> R.string.chat_error_enviar
    }

    fun esSoloLectura(e: Exception): Boolean = e is ErrorApi && e.codigo == 409

    @StringRes
    private fun mensajeAlAbrir(codigo: Int): Int = when (codigo) {
        401 -> R.string.chat_error_sesion
        403 -> R.string.chat_error_sin_permiso
        404 -> R.string.chat_error_no_existe
        409 -> R.string.chat_error_sin_agente
        else -> R.string.chat_error_generico
    }

    // ---------- helpers privados ----------

    private fun MensajeDto.aModelo(): MensajeChat {
        val rolMensaje = rol?.let { valor -> RolChat.entries.firstOrNull { it.name == valor } }
        return MensajeChat(
            clientMessageId = clientMessageId ?: "srv-$secuencia",
            secuencia = secuencia,
            autor = autor.orEmpty(),
            rol = rolMensaje,
            texto = texto,
            enviadoEn = enviadoEn?.let { runCatching { LocalDateTime.parse(it) }.getOrNull() } ?: LocalDateTime.now(),
            propio = rolMensaje == rolPropio
        )
    }
}
