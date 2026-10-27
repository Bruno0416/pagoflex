package com.pagoflex.app.features.cumplimiento.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Icon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.cumplimiento.data.CategoriaCumplimiento
import com.pagoflex.app.features.cumplimiento.data.ResultadoCumplimiento

@Composable
internal fun CumplimientoContent(
    resultado: ResultadoCumplimiento,
    onVolver: () -> Unit,
    onVerReconocimientos: (() -> Unit)?
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cumplimiento_titulo), style = Theme.type.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver,
                        variant = ComponentVariant.Secondary,
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(R.string.cumplimiento_volver)
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Theme.colores.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .verticalScroll(rememberScrollState())
                .padding(Theme.dimens.paddingSides),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingL)
        ) {
            // 1. mostrar indicador o historial insuficiente
            Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Theme.dimens.spacingM),
                    verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingL),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (resultado.porcentaje == null) {
                        Text(
                            stringResource(R.string.cumplimiento_insuficiente_titulo),
                            style = Theme.type.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            stringResource(R.string.cumplimiento_insuficiente_detalle, resultado.totalElegibles),
                            style = Theme.type.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        IndicadorCumplimiento(resultado)
                        Text(
                            stringResource(
                                R.string.cumplimiento_resumen,
                                resultado.pagadosATiempo,
                                resultado.totalElegibles
                            ),
                            style = Theme.type.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
            }

            HorizontalDivider(color = Theme.colores.outlineVariant)

            // 2. explicar categorias y privacidad
            EscalaCumplimiento(resultado.categoria)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = Theme.formas.large,
                color = Theme.colores.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(Theme.dimens.spacingM),
                    horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_visibility),
                        contentDescription = null,
                        tint = Theme.colores.onPrimaryContainer
                    )
                    Text(
                        stringResource(R.string.cumplimiento_privacidad),
                        color = Theme.colores.onPrimaryContainer,
                        style = Theme.type.bodyMedium
                    )
                }
            }
            Text(
                stringResource(R.string.cumplimiento_exclusiones),
                color = Theme.colores.onSurfaceVariant,
                style = Theme.type.bodyMedium
            )

            // 3. preparar el acceso a reconocimientos
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onVerReconocimientos?.invoke() },
                enabled = onVerReconocimientos != null
            ) {
                Text(stringResource(R.string.cumplimiento_reconocimientos))
            }
            Spacer(Modifier.padding(Theme.dimens.spacingXS))
        }
    }
}

// ---------- previews ----------
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CumplimientoContentPreview() {
    PagoFlexTheme {
        CumplimientoContent(
            resultado = ResultadoCumplimiento(36, 33, 91.6667, CategoriaCumplimiento.AL_DIA),
            onVolver = {},
            onVerReconocimientos = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CumplimientoInsuficientePreview() {
    PagoFlexTheme {
        CumplimientoContent(
            resultado = ResultadoCumplimiento(totalElegibles = 2, pagadosATiempo = 1),
            onVolver = {},
            onVerReconocimientos = null
        )
    }
}
