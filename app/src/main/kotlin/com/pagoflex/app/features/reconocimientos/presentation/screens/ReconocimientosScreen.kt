package com.pagoflex.app.features.reconocimientos.presentation.screens

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.components.feedback.CircularProgressIndicator
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.reconocimientos.data.ReconocimientoObtenidoRow
import com.pagoflex.app.features.reconocimientos.data.ReconocimientosRepository
import com.pagoflex.app.features.reconocimientos.presentation.components.CardReconocimiento
import com.pagoflex.app.features.reconocimientos.presentation.viewmodel.ReconocimientosViewModel
import kotlin.collections.forEach

@Composable
fun ReconocimientosScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onVolver: () -> Unit
) {
    val repositorio = remember(db) { ReconocimientosRepository(db) }
    val viewModel: ReconocimientosViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ReconocimientosViewModel(repositorio) }
        }
    )
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. actualizar los logros al abrir la pantalla
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarReconocimientos(usuarioId)
    }

    ReconocimientosContent(
        reconocimientos = estado.reconocimientos,
        cargando = estado.cargando,
        mensajeError = estado.mensajeError,
        onVolver = onVolver
    )
}


@Composable
internal fun ReconocimientosContent(
    reconocimientos: List<ReconocimientoObtenidoRow>,
    cargando: Boolean,
    @StringRes mensajeError: Int?,
    onVolver: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reconocimientos_titulo), style = Theme.type.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver,
                        variant = ComponentVariant.Secondary,
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(R.string.reconocimientos_volver)
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Theme.colores.background)
            )
        }
    ) { padding ->
        if (cargando || mensajeError != null) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (cargando) CircularProgressIndicator()
                else Text(stringResource(checkNotNull(mensajeError)))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(Theme.dimens.paddingSides),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingL)
            ) {
                if (reconocimientos.isEmpty()) {
                    // 1. explicar el estado sin reconocimientos
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = Theme.formas.large,
                        color = Theme.colores.primaryContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(Theme.dimens.spacingL),
                            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
                        ) {
                            Text(
                                stringResource(R.string.reconocimientos_vacio_titulo),
                                style = Theme.type.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Theme.colores.onPrimaryContainer
                            )
                            Text(
                                stringResource(R.string.reconocimientos_vacio_descripcion),
                                style = Theme.type.bodyLarge,
                                color = Theme.colores.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    // 1. mostrar los reconocimientos realmente obtenidos
                    reconocimientos.forEach { reconocimiento ->
                        CardReconocimiento(reconocimiento)
                    }
                }
                Text(
                    stringResource(R.string.reconocimientos_privacidad),
                    style = Theme.type.labelMedium,
                    color = Theme.colores.onSurfaceVariant
                )
            }
        }
    }
}

// ----------------- PREVIEWS -----------------
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReconocimientosVacioPreview() {
    PagoFlexTheme {
        ReconocimientosContent(emptyList(), false, null, {})
    }
}
