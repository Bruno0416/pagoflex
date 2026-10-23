package com.pagoflex.app.features.reportes.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.buttons.TextButton
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.model.ComponentSize
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aPesos
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.presentation.model.CompromisoReportable
import com.pagoflex.app.features.reportes.presentation.model.ReporteItem
import com.pagoflex.app.features.reportes.presentation.viewmodel.MisReportesViewModel

@Composable
fun MisReportesScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onReportar: (String) -> Unit,
    onCerrarSesion: () -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { ReportesRepository(db) }
    val viewModel: MisReportesViewModel = viewModel(
        factory = viewModelFactory {
            initializer { MisReportesViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // recargar al volver de reportar
    LaunchedEffect(usuarioId) {
        viewModel.cargar(usuarioId)
    }

    if (uiState.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // ---------- contenido ----------
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.dimens.spacingM),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
    ) {
        item {
            Text(
                stringResource(R.string.rep_ayuda_titulo),
                style = Theme.type.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                stringResource(R.string.rep_ayuda_texto),
                style = Theme.type.bodyMedium,
                color = Theme.colores.onSurfaceVariant
            )
        }

        // ---------- mis reportes ----------
        if (uiState.reportes.isNotEmpty()) {
            item { Seccion(stringResource(R.string.rep_mis_reportes)) }
            items(uiState.reportes, key = { it.numero }) { CardMiReporte(it) }
        }

        // ---------- cobros vigentes ----------
        item { Seccion(stringResource(R.string.rep_problemas_cobro)) }
        if (uiState.compromisos.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.rep_sin_compromisos),
                    style = Theme.type.bodyMedium,
                    color = Theme.colores.onSurfaceVariant
                )
            }
        }
        items(uiState.compromisos, key = { it.folio }) { compromiso ->
            CardCompromisoReportable(compromiso, onReportar = { onReportar(compromiso.folio) })
        }

        item {
            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.padding(top = Theme.dimens.spacingL)
            ) {
                Text(stringResource(R.string.ui_cerrar_sesion))
            }
        }
    }
}

@Composable
private fun Seccion(texto: String) {
    Text(
        text = texto,
        style = Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(top = Theme.dimens.spacingS)
    )
}

// reporte propio con su estado y la respuesta del agente (HU-06 CA-3)
@Composable
private fun CardMiReporte(reporte: ReporteItem) {
    Card(variant = ComponentVariant.Primary, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Theme.dimens.paddingCardsCompromiso),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXXS)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(reporte.empresa, style = Theme.type.titleMedium, modifier = Modifier.weight(1f))
                StatusChip(label = stringResource(reporte.estado.labelRes), colores = reporte.estado.coloresCard)
            }
            Text(
                stringResource(
                    R.string.rep_reporte_linea,
                    stringResource(reporte.motivo.labelRes),
                    reporte.numero,
                    reporte.fechaCreacion.toLocalDate().aTexto()
                ),
                style = Theme.type.bodySmall,
                color = Theme.colores.onSurfaceVariant
            )
            when (reporte.estado) {
                EstadoReporte.ABIERTO -> Text(stringResource(R.string.rep_estado_abierto_usuario), style = Theme.type.bodyMedium)
                EstadoReporte.EN_GESTION -> Text(stringResource(R.string.rep_estado_gestion_usuario), style = Theme.type.bodyMedium)
                EstadoReporte.RESUELTO -> {
                    Text(stringResource(R.string.rep_respuesta_soporte), style = Theme.type.labelLarge)
                    Text(reporte.respuesta.orEmpty(), style = Theme.type.bodyMedium)
                    Text(
                        stringResource(
                            R.string.rep_compromiso_quedo,
                            stringResource(reporte.estadoCompromiso.labelRes).lowercase()
                        ),
                        style = Theme.type.bodySmall,
                        color = Theme.colores.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CardCompromisoReportable(compromiso: CompromisoReportable, onReportar: () -> Unit) {
    Card(variant = ComponentVariant.Secondary, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Theme.dimens.paddingCardsCompromiso),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(compromiso.empresa, style = Theme.type.titleSmall)
                Text(
                    stringResource(R.string.rep_concepto_total, compromiso.concepto, compromiso.total.aPesos()),
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant
                )
            }
            TextButton(onClick = onReportar, size = ComponentSize.Small) {
                Text(
                    stringResource(
                        if (compromiso.estado == EstadoCompromiso.EN_REVISION) R.string.rep_ver_reporte
                        else R.string.rep_reportar
                    )
                )
            }
        }
    }
}
