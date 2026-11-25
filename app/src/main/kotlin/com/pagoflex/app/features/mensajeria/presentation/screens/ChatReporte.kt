package com.pagoflex.app.features.mensajeria.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.components.buttons.TextButton
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.feedback.LinearProgressIndicator
import com.pagoflex.app.core.ui.components.feedback.SnackbarHost
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.model.ComponentSize
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.features.mensajeria.data.MensajeriaRepository
import com.pagoflex.app.features.mensajeria.data.remote.MensajeriaRemoto
import com.pagoflex.app.features.mensajeria.presentation.model.ChatUiState
import com.pagoflex.app.features.mensajeria.presentation.model.EstadoEnvio
import com.pagoflex.app.features.mensajeria.presentation.model.MensajeChat
import com.pagoflex.app.features.mensajeria.presentation.model.RolChat
import com.pagoflex.app.features.mensajeria.presentation.viewmodel.ChatViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoHora = DateTimeFormatter.ofPattern("HH:mm")

// chat a pantalla completa sobre la pantalla actual, sin tocar la navegación
@Composable
fun ChatDialog(
    numero: String,
    rolPropio: RolChat,
    onCerrar: () -> Unit,
) {
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        ChatReporte(numero = numero, rolPropio = rolPropio, onCerrar = onCerrar)
    }
}

@Composable
fun ChatReporte(
    numero: String,
    rolPropio: RolChat,
    onCerrar: () -> Unit,
) {
    // ---------- dependencias ----------
    // el viewmodel vive solo mientras el chat está abierto: al cerrarlo se corta el canal en vivo
    val dueno = remember(numero) {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(dueno) {
        onDispose { dueno.viewModelStore.clear() }
    }
    val repositorio = remember(numero, rolPropio) {
        MensajeriaRepository(MensajeriaRemoto.crear(), rolPropio)
    }
    val viewModel: ChatViewModel = viewModel(
        viewModelStoreOwner = dueno,
        key = "chat-$numero",
        factory = viewModelFactory {
            initializer { ChatViewModel(repositorio, numero, rolPropio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val resources = LocalResources.current

    // escuchar en vivo solo con la app a la vista
    LifecycleStartEffect(viewModel) {
        viewModel.conectar()
        onStopOrDispose { viewModel.desconectar() }
    }

    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let {
            snackbar.showSnackbar(resources.getString(it))
            viewModel.avisoMostrado()
        }
    }

    // ---------- contenido ----------
    Surface(modifier = Modifier.fillMaxSize(), color = Theme.colores.background) {
        Scaffold(
            modifier = Modifier.safeDrawingPadding(),
            containerColor = Theme.colores.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                Column {
                    BarraSuperior(
                        titulo = stringResource(R.string.chat_titulo),
                        subtitulo = stringResource(R.string.chat_subtitulo, numero),
                        onVolver = onCerrar
                    )
                    if (uiState.reconectando) AvisoReconectando()
                }
            },
            bottomBar = {
                if (!uiState.cargando && uiState.error == null) {
                    if (uiState.soloLectura) {
                        AvisoSoloLectura()
                    } else {
                        CampoMensaje(
                            uiState = uiState,
                            onTexto = viewModel::setTexto,
                            onEnviar = viewModel::enviar
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbar) }
        ) { padding ->
            val error = uiState.error

            when {
                uiState.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                error != null -> ErrorChat(
                    mensaje = stringResource(error),
                    onReintentar = viewModel::cargar,
                    modifier = Modifier.padding(padding)
                )

                else -> ListaMensajes(
                    uiState = uiState,
                    onCargarAnteriores = viewModel::cargarAnteriores,
                    onReintentar = viewModel::reintentar,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ListaMensajes(
    uiState: ChatUiState,
    onCargarAnteriores: () -> Unit,
    onReintentar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val estadoLista = rememberLazyListState()
    val mensajes = uiState.mensajes
    // la cabecera de "cargar anteriores" corre el índice en uno
    val desfase = if (uiState.cursorAnterior != null) 1 else 0

    // bajar al último solo cuando llega uno nuevo, no al cargar anteriores
    val ultimo = mensajes.lastOrNull()?.clientMessageId
    LaunchedEffect(ultimo) {
        if (mensajes.isNotEmpty()) estadoLista.animateScrollToItem(mensajes.lastIndex + desfase)
    }

    if (mensajes.isEmpty() && uiState.cursorAnterior == null) {
        Box(modifier.fillMaxSize().padding(Theme.dimens.spacingL), contentAlignment = Alignment.Center) {
            Text(
                stringResource(if (uiState.soloLectura) R.string.chat_vacio_lectura else R.string.chat_vacio),
                style = Theme.type.bodyMedium,
                color = Theme.colores.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyColumn(
        state = estadoLista,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Theme.dimens.spacingM, vertical = Theme.dimens.spacingS),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXS)
    ) {
        // ---------- mensajes anteriores ----------
        if (uiState.cursorAnterior != null) {
            item(key = "anteriores") {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (uiState.cargandoAnteriores) {
                        CircularProgressIndicator(Modifier.padding(Theme.dimens.spacingXS))
                    } else {
                        TextButton(onClick = onCargarAnteriores, size = ComponentSize.Small) {
                            Text(stringResource(R.string.chat_cargar_anteriores))
                        }
                    }
                }
            }
        }

        // ---------- burbujas ----------
        items(mensajes, key = { it.secuencia?.let { s -> "s$s" } ?: "c${it.clientMessageId}" }) { mensaje ->
            Burbuja(mensaje = mensaje, onReintentar = { onReintentar(mensaje.clientMessageId) })
        }
    }
}

// las propias a la derecha según el rol de quien mira
@Composable
private fun Burbuja(
    mensaje: MensajeChat,
    onReintentar: () -> Unit,
) {
    val forma = if (mensaje.propio) {
        RoundedCornerShape(Theme.dimens.radiusS, Theme.dimens.radiusS, Theme.dimens.radiusXXS, Theme.dimens.radiusS)
    } else {
        RoundedCornerShape(Theme.dimens.radiusS, Theme.dimens.radiusS, Theme.dimens.radiusS, Theme.dimens.radiusXXS)
    }
    val fondo = when {
        mensaje.estado == EstadoEnvio.ERROR -> Theme.colores.errorContainer
        mensaje.propio -> Theme.colores.primaryContainer
        else -> Theme.colores.surfaceContainerHigh
    }
    val textoColor = when {
        mensaje.estado == EstadoEnvio.ERROR -> Theme.colores.onErrorContainer
        mensaje.propio -> Theme.colores.onPrimaryContainer
        else -> Theme.colores.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.propio) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (mensaje.propio) Alignment.End else Alignment.Start
        ) {
            // autor y hora pequeñitos sobre la burbuja
            Text(
                stringResource(R.string.chat_autor_hora, nombreAutor(mensaje), hora(mensaje)),
                style = Theme.type.labelSmall,
                color = Theme.colores.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Theme.dimens.spacingXS, vertical = Theme.dimens.spacingXXXS)
            )
            Surface(
                shape = forma,
                color = fondo,
                modifier = if (mensaje.estado == EstadoEnvio.ERROR) Modifier.clickable(onClick = onReintentar) else Modifier
            ) {
                Text(
                    mensaje.texto,
                    style = Theme.type.bodyMedium,
                    color = textoColor,
                    modifier = Modifier.padding(horizontal = Theme.dimens.spacingM, vertical = Theme.dimens.spacingS)
                )
            }
            when (mensaje.estado) {
                EstadoEnvio.ENVIANDO -> Text(
                    stringResource(R.string.chat_enviando),
                    style = Theme.type.labelSmall,
                    color = Theme.colores.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Theme.dimens.spacingXS)
                )

                EstadoEnvio.ERROR -> TextButton(
                    onClick = onReintentar,
                    variant = ComponentVariant.Destructive,
                    size = ComponentSize.Small
                ) {
                    Text(stringResource(R.string.chat_no_enviado), style = Theme.type.labelSmall)
                }

                EstadoEnvio.ENVIADO -> Unit
            }
        }
    }
}

@Composable
private fun CampoMensaje(
    uiState: ChatUiState,
    onTexto: (String) -> Unit,
    onEnviar: () -> Unit,
) {
    Surface(color = Theme.colores.surfaceContainer) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.dimens.spacingS, vertical = Theme.dimens.spacingXS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = uiState.texto,
                onValueChange = onTexto,
                placeholder = stringResource(R.string.chat_placeholder),
                singleLine = false,
                error = if (uiState.textoMuyLargo) {
                    stringResource(R.string.chat_contador, uiState.texto.trim().length, ChatUiState.LARGO_MAXIMO)
                } else {
                    null
                },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onEnviar,
                enabled = uiState.puedeEnviar,
                iconRes = R.drawable.ic_send,
                description = stringResource(R.string.chat_enviar)
            )
        }
    }
}

@Composable
private fun AvisoSoloLectura() {
    Card(
        variant = ComponentVariant.Secondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.dimens.spacingM)
    ) {
        Text(
            stringResource(R.string.chat_solo_lectura),
            style = Theme.type.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.dimens.spacingM)
        )
    }
}

@Composable
private fun AvisoReconectando() {
    Column(Modifier.fillMaxWidth()) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Text(
            stringResource(R.string.chat_reconectando),
            style = Theme.type.labelSmall,
            color = Theme.colores.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Theme.dimens.spacingXXS)
        )
    }
}

@Composable
private fun ErrorChat(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Theme.dimens.spacingL),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(mensaje, style = Theme.type.bodyLarge, textAlign = TextAlign.Center)
        Button(onClick = onReintentar, variant = ComponentVariant.Secondary) {
            Text(stringResource(R.string.chat_reintentar))
        }
    }
}

// ---------- helpers ----------

@Composable
private fun nombreAutor(mensaje: MensajeChat): String = when {
    mensaje.propio -> stringResource(R.string.chat_tu)
    mensaje.autor.isNotBlank() -> mensaje.autor
    mensaje.rol == RolChat.AGENTE -> stringResource(R.string.chat_rol_agente)
    else -> stringResource(R.string.chat_rol_ejecutivo)
}

// solo la hora si es de hoy
private fun hora(mensaje: MensajeChat): String =
    if (mensaje.enviadoEn.toLocalDate() == LocalDate.now()) mensaje.enviadoEn.format(formatoHora)
    else mensaje.enviadoEn.aTexto()
