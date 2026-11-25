package com.pagoflex.app.features.mensajeria.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagoflex.app.R
import com.pagoflex.app.features.mensajeria.data.EventoChat
import com.pagoflex.app.features.mensajeria.data.MensajeriaRepository
import com.pagoflex.app.features.mensajeria.presentation.model.ChatUiState
import com.pagoflex.app.features.mensajeria.presentation.model.EstadoEnvio
import com.pagoflex.app.features.mensajeria.presentation.model.MensajeChat
import com.pagoflex.app.features.mensajeria.presentation.model.RolChat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class ChatViewModel(
    private val repositorio: MensajeriaRepository,
    private val numero: String,
    private val rolPropio: RolChat,
    private val ahora: () -> LocalDateTime = LocalDateTime::now,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // canal en vivo; solo corre mientras el chat está a la vista
    private var suscripcion: Job? = null
    private var visible = false
    private var cargado = false

    // mayor secuencia vista y la última que se avisó como leída
    private var ultimaSecuencia = 0L
    private var ultimaLeida = 0L

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            try {
                // 1. abrir la conversación y traer los últimos mensajes
                val conversacion = repositorio.abrir(numero)
                val pagina = repositorio.historial(numero)
                ultimaLeida = conversacion.ultimaSecuenciaLeida
                ultimaSecuencia = pagina.mensajes.mapNotNull { it.secuencia }.maxOrNull() ?: 0L

                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajes = fusionar(it.mensajes, pagina.mensajes),
                        cursorAnterior = pagina.cursorAnterior,
                        soloLectura = conversacion.soloLectura
                    )
                }
                cargado = true

                // 2. marcar leído y escuchar los nuevos
                marcarLeido()
                if (visible) suscribir()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = repositorio.errorAlAbrir(e)) }
            }
        }
    }

    // ---------- canal en vivo ----------

    fun conectar() {
        visible = true
        if (cargado && suscripcion?.isActive != true) suscribir()
    }

    fun desconectar() {
        visible = false
        suscripcion?.cancel()
        suscripcion = null
        _uiState.update { it.copy(conectado = false, reconectando = false) }
    }

    private fun suscribir() {
        suscripcion?.cancel()
        suscripcion = viewModelScope.launch {
            // al reconectar el backend reenvía lo posterior a la última secuencia
            repositorio.eventos(numero, ultimaSecuencia).collect { evento ->
                when (evento) {
                    EventoChat.Conectado -> _uiState.update { it.copy(conectado = true, reconectando = false) }
                    EventoChat.Reconectando -> _uiState.update { it.copy(conectado = false, reconectando = true) }
                    is EventoChat.Nuevo -> {
                        agregar(listOf(evento.mensaje))
                        marcarLeido()
                    }

                    is EventoChat.Rechazado -> _uiState.update {
                        it.copy(conectado = false, reconectando = false, aviso = evento.mensaje)
                    }
                }
            }
        }
    }

    // ---------- escribir ----------

    fun setTexto(texto: String) {
        _uiState.update { it.copy(texto = texto) }
    }

    // envío optimista: la burbuja aparece al tiro y se confirma o queda con error
    fun enviar() {
        val estado = uiState.value
        val texto = estado.texto.trim()
        if (estado.soloLectura || estado.error != null || texto.isEmpty()) return
        if (estado.textoMuyLargo) {
            _uiState.update { it.copy(aviso = R.string.chat_error_texto) }
            return
        }

        // 1. burbuja provisoria con su id para no duplicar al reintentar
        val pendiente = MensajeChat(
            clientMessageId = UUID.randomUUID().toString(),
            secuencia = null,
            autor = "",
            rol = rolPropio,
            texto = texto,
            enviadoEn = ahora(),
            propio = true,
            estado = EstadoEnvio.ENVIANDO
        )
        _uiState.update { it.copy(texto = "", mensajes = it.mensajes + pendiente) }

        // 2. enviar al backend
        mandar(pendiente)
    }

    // se reintenta con el mismo clientMessageId: el backend no lo duplica
    fun reintentar(clientMessageId: String) {
        val mensaje = uiState.value.mensajes.firstOrNull {
            it.clientMessageId == clientMessageId && it.estado == EstadoEnvio.ERROR
        } ?: return
        if (uiState.value.soloLectura) return
        cambiarEstado(clientMessageId, EstadoEnvio.ENVIANDO)
        mandar(mensaje)
    }

    private fun mandar(mensaje: MensajeChat) {
        viewModelScope.launch {
            try {
                val confirmado = repositorio.enviar(numero, mensaje.texto, mensaje.clientMessageId)
                agregar(listOf(confirmado))
                marcarLeido()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                cambiarEstado(mensaje.clientMessageId, EstadoEnvio.ERROR)
                // 409 al escribir: el reporte quedó resuelto
                val soloLectura = repositorio.esSoloLectura(e)
                _uiState.update {
                    it.copy(aviso = repositorio.errorAlEnviar(e), soloLectura = it.soloLectura || soloLectura)
                }
            }
        }
    }

    // ---------- historial ----------

    fun cargarAnteriores() {
        val estado = uiState.value
        val cursor = estado.cursorAnterior ?: return
        if (estado.cargandoAnteriores) return
        _uiState.update { it.copy(cargandoAnteriores = true) }

        viewModelScope.launch {
            try {
                val pagina = repositorio.historial(numero, cursor)
                _uiState.update {
                    it.copy(mensajes = fusionar(it.mensajes, pagina.mensajes), cursorAnterior = pagina.cursorAnterior)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(aviso = repositorio.errorAlAbrir(e)) }
            } finally {
                _uiState.update { it.copy(cargandoAnteriores = false) }
            }
        }
    }

    fun avisoMostrado() {
        _uiState.update { it.copy(aviso = null) }
    }

    override fun onCleared() {
        suscripcion?.cancel()
        super.onCleared()
    }

    // ---------- helpers privados ----------

    private fun agregar(nuevos: List<MensajeChat>) {
        nuevos.mapNotNull { it.secuencia }.maxOrNull()?.let { if (it > ultimaSecuencia) ultimaSecuencia = it }
        _uiState.update { it.copy(mensajes = fusionar(it.mensajes, nuevos)) }
    }

    // sin duplicados por secuencia ni por clientMessageId; los pendientes quedan al final
    private fun fusionar(actuales: List<MensajeChat>, nuevos: List<MensajeChat>): List<MensajeChat> {
        val confirmados = LinkedHashMap<Long, MensajeChat>()
        val pendientes = LinkedHashMap<String, MensajeChat>()

        for (mensaje in actuales + nuevos) {
            val secuencia = mensaje.secuencia
            if (secuencia == null) {
                pendientes[mensaje.clientMessageId] = mensaje
            } else {
                // el confirmado reemplaza a su burbuja provisoria
                pendientes.remove(mensaje.clientMessageId)
                confirmados.putIfAbsent(secuencia, mensaje)
            }
        }

        // un pendiente cuyo id ya llegó confirmado no se muestra dos veces
        val idsConfirmados = confirmados.values.map { it.clientMessageId }.toSet()
        return confirmados.values.sortedBy { it.secuencia } +
                pendientes.values.filter { it.clientMessageId !in idsConfirmados }
    }

    private fun cambiarEstado(clientMessageId: String, estado: EstadoEnvio) {
        _uiState.update { actual ->
            actual.copy(mensajes = actual.mensajes.map {
                if (it.clientMessageId == clientMessageId && it.secuencia == null) it.copy(estado = estado) else it
            })
        }
    }

    private fun marcarLeido() {
        val hasta = ultimaSecuencia
        if (hasta <= ultimaLeida) return
        ultimaLeida = hasta
        viewModelScope.launch {
            try {
                repositorio.marcarLeido(numero, hasta)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // si falla se vuelve a intentar con el próximo mensaje
                if (ultimaLeida == hasta) ultimaLeida = 0L
            }
        }
    }
}
