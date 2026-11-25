package com.pagoflex.app.features.mensajeria.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.remote.llamarONulo
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.mensajeria.data.remote.MensajeriaApi
import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.mensajeria.presentation.model.ConversacionActiva
import com.pagoflex.app.features.mensajeria.presentation.model.RolChat
import com.pagoflex.app.features.mensajeria.presentation.viewmodel.BandejaMensajesViewModel
import com.pagoflex.app.features.reportes.data.remote.ReportesRemoto
import java.time.LocalDate

@Composable
fun BandejaMensajesScreen(rol: RolChat) {
    val contexto = LocalContext.current.applicationContext
    val viewModel: BandejaMensajesViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val reportes = ReportesRemoto.crear()
                val mensajeria = RetrofitClient.retrofit.create(MensajeriaApi::class.java)
                BandejaMensajesViewModel(
                    reportes = {
                        if (rol == RolChat.AGENTE) reportes.bandeja(LocalDate.now())
                        else reportes.reportesEmpresa(LocalDate.now())
                    },
                    conversacion = { numero -> llamarONulo { mensajeria.conversacion(numero) } }
                )
            }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var chatAbierto by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.cargar() }

    Scaffold(
        containerColor = Theme.colores.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            BarraSuperior(
                titulo = stringResource(R.string.msj_titulo),
                subtitulo = stringResource(R.string.msj_subtitulo)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.error != null -> Text(
                    stringResource(uiState.error!!),
                    modifier = Modifier.align(Alignment.Center).padding(Theme.dimens.paddingSides),
                    color = Theme.colores.error
                )
                uiState.conversaciones.isEmpty() -> Text(
                    stringResource(R.string.msj_vacio),
                    modifier = Modifier.align(Alignment.Center).padding(Theme.dimens.paddingSides),
                    color = Theme.colores.onSurfaceVariant
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = Theme.dimens.paddingSides,
                    verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
                ) {
                    items(uiState.conversaciones, key = { it.numero }) { conversacion ->
                        TarjetaConversacion(conversacion) { chatAbierto = conversacion.numero }
                    }
                }
            }
        }
    }

    chatAbierto?.let { numero ->
        ChatDialog(
            numero = numero,
            rolPropio = rol,
            onCerrar = {
                chatAbierto = null
                viewModel.cargar()
            }
        )
    }
}

@Composable
private fun TarjetaConversacion(conversacion: ConversacionActiva, onAbrir: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onAbrir) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Theme.dimens.spacingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chat_bubble),
                contentDescription = null,
                tint = Theme.colores.primary
            )
            Column(Modifier.weight(1f)) {
                Text(
                    "${conversacion.numero} · ${conversacion.folio}",
                    style = Theme.type.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${conversacion.concepto} · ${conversacion.empresa}",
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant
                )
                Text(
                    pluralStringResource(R.plurals.msj_cantidad, conversacion.mensajes.toInt(), conversacion.mensajes.toInt()),
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant
                )
            }
            if (conversacion.sinLeer > 0) {
                Badge { Text(conversacion.sinLeer.toString()) }
            }
        }
    }
}
