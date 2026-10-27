package com.pagoflex.app.features.reportes.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import com.pagoflex.app.core.rules.MotivoReporte
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.containers.FilaDato
import com.pagoflex.app.core.ui.components.containers.SeccionDatos
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.components.selection.RadioButton
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aPesos
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.presentation.model.ReportarProblemaUiState
import com.pagoflex.app.features.reportes.presentation.model.ReporteItem
import com.pagoflex.app.features.reportes.presentation.viewmodel.ReportarProblemaViewModel

@Composable
fun ReportarProblemaScreen(
    db: LocalDatabase,
    usuarioId: Int,
    folio: String,
    onVolver: () -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { ReportesRepository(db) }
    val viewModel: ReportarProblemaViewModel = viewModel(
        key = "reportar-$folio",
        factory = viewModelFactory {
            initializer { ReportarProblemaViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(folio) {
        viewModel.cargar(usuarioId, folio)
    }

    // ---------- contenido ----------
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { BarraSuperior(titulo = stringResource(R.string.rep_titulo_reportar), onVolver = onVolver) }
    ) { padding ->
        when {
            uiState.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            uiState.compromiso == null -> Text(
                stringResource(R.string.rep_error_no_encontrado),
                modifier = Modifier.padding(padding).padding(Theme.dimens.spacingL)
            )

            else -> ReportarContent(
                uiState = uiState,
                onMotivo = viewModel::setMotivo,
                onComentario = viewModel::setComentario,
                onEnviar = { viewModel.enviar(usuarioId) },
                onVolver = onVolver,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ReportarContent(
    uiState: ReportarProblemaUiState,
    onMotivo: (MotivoReporte) -> Unit,
    onComentario: (String) -> Unit,
    onEnviar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val compromiso = uiState.compromiso ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            // imePadding antes del scroll para que el teclado no tape el comentario
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(Theme.dimens.paddingSides)
            .padding(bottom = Theme.dimens.spacingXL),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
    ) {
        SeccionDatos(titulo = compromiso.empresa) {
            FilaDato(
                stringResource(R.string.rep_campo_concepto),
                compromiso.textoCuota?.let { "${compromiso.concepto} · $it" } ?: compromiso.concepto
            )
            FilaDato(stringResource(R.string.rep_campo_folio), compromiso.folio)
            FilaDato(stringResource(R.string.rep_campo_vencimiento), compromiso.fechaVencimiento.aTexto())
            FilaDato(stringResource(R.string.rep_campo_total), compromiso.total.aPesos(), destacado = true)
        }

        // ya existe un reporte: mostrarlo en vez de crear otro (HU-06 CA-2)
        val activo = uiState.reporteActivo
        if (activo != null) {
            ReporteExistente(activo, recienEnviado = uiState.recienEnviado)
            OutlinedButton(onClick = onVolver) { Text(stringResource(R.string.rep_volver)) }
            return@Column
        }

        Text(stringResource(R.string.rep_que_pasa), style = Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold))
        Column {
            MotivoReporte.entries.forEach { motivo ->
                RadioButton(
                    selected = uiState.motivo == motivo,
                    onClick = { onMotivo(motivo) },
                    label = stringResource(motivo.labelRes)
                )
            }
        }

        TextField(
            value = uiState.comentario,
            onValueChange = onComentario,
            label = stringResource(
                if (uiState.motivo == MotivoReporte.OTRO) R.string.rep_comentario_obligatorio
                else R.string.rep_comentario_opcional
            ),
            singleLine = false,
            error = uiState.error?.let { stringResource(it) }
        )

        Text(
            stringResource(R.string.rep_aviso_revision),
            style = Theme.type.bodySmall,
            color = Theme.colores.onSurfaceVariant
        )

        Button(onClick = onEnviar, enabled = uiState.motivo != null && !uiState.enviando) {
            Text(stringResource(R.string.rep_enviar))
        }
    }
}

@Composable
private fun ReporteExistente(reporte: ReporteItem, recienEnviado: Boolean) {
    Card(variant = ComponentVariant.Secondary, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Theme.dimens.spacingM),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXS)
        ) {
            Text(
                text = stringResource(if (recienEnviado) R.string.rep_enviado_titulo else R.string.rep_ya_existe_titulo),
                style = Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            StatusChip(label = stringResource(reporte.estado.labelRes), colores = reporte.estado.coloresCard)
            FilaDato(stringResource(R.string.rep_campo_numero), reporte.numero)
            FilaDato(stringResource(R.string.rep_campo_motivo), stringResource(reporte.motivo.labelRes))
            FilaDato(stringResource(R.string.rep_campo_enviado), reporte.fechaCreacion.aTexto())
            Text(
                stringResource(R.string.rep_te_avisaremos),
                style = Theme.type.bodySmall,
                color = Theme.colores.onSurfaceVariant
            )
        }
    }
}
