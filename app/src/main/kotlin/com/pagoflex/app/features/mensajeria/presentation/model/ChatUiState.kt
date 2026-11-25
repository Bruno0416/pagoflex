package com.pagoflex.app.features.mensajeria.presentation.model

import androidx.annotation.StringRes
import java.time.LocalDateTime

// quién mira el chat: sus mensajes van a la derecha
enum class RolChat { AGENTE, EJECUTIVO }

enum class EstadoEnvio { ENVIANDO, ENVIADO, ERROR }

// una burbuja; mientras se envía todavía no tiene secuencia
data class MensajeChat(
    val clientMessageId: String,
    val secuencia: Long?,
    val autor: String,
    val rol: RolChat?,
    val texto: String,
    val enviadoEn: LocalDateTime,
    val propio: Boolean,
    val estado: EstadoEnvio = EstadoEnvio.ENVIADO,
)

// datos de la conversación al abrirla
data class Conversacion(
    val numero: String,
    val soloLectura: Boolean,
    val ultimaSecuencia: Long,
    val ultimaSecuenciaLeida: Long,
)

// una página del historial
data class PaginaChat(
    val mensajes: List<MensajeChat>,
    val cursorAnterior: Long?,
)

// chat entre el agente y el ejecutivo sobre un reporte
data class ChatUiState(
    val cargando: Boolean = true,
    val mensajes: List<MensajeChat> = emptyList(),
    val texto: String = "",
    val soloLectura: Boolean = false,

    // canal en vivo
    val conectado: Boolean = false,
    val reconectando: Boolean = false,

    // mensajes anteriores
    val cursorAnterior: Long? = null,
    val cargandoAnteriores: Boolean = false,

    // error que impide abrir el chat y avisos de una sola vez
    @StringRes val error: Int? = null,
    @StringRes val aviso: Int? = null,
) {
    val textoMuyLargo: Boolean
        get() = texto.trim().length > LARGO_MAXIMO

    val puedeEnviar: Boolean
        get() = !soloLectura && error == null && texto.isNotBlank() && !textoMuyLargo

    companion object {
        const val LARGO_MAXIMO = 1000
    }
}
