package com.pagoflex.app.features.reportes.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.rules.EstadoReporte
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.containers.FilaDato
import com.pagoflex.app.core.ui.components.containers.SeccionDatos
import com.pagoflex.app.core.ui.components.feedback.SnackbarHost
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.styles.coloresAtrasado
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aPesos
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.features.reportes.data.ReportesRepository
import com.pagoflex.app.features.reportes.presentation.model.DetalleReporteUiState
import com.pagoflex.app.features.reportes.presentation.model.ReporteItem
import com.pagoflex.app.features.reportes.presentation.viewmodel.DetalleReporteViewModel

@Composable
fun DetalleReporteScreen(
    db: LocalDatabase,
    usuarioId: Int,
    numero: String,
    onVolver: () -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { ReportesRepository(db) }
    val viewModel: DetalleReporteViewModel = viewModel(
        key = "reporte-$numero",
        factory = viewModelFactory {
            initializer { DetalleReporteViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(numero) {
        viewModel.cargar(numero)
    }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbar.showSnackbar(context.getString(it))
            viewModel.mensajeMostrado()
        }
    }

    // ---------- contenido ----------
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            BarraSuperior(titulo = stringResource(R.string.rep_titulo_detalle), subtitulo = numero, onVolver = onVolver)
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        val reporte = uiState.reporte

        when {
            uiState.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            reporte == null -> Text(
                stringResource(R.string.rep_error_reporte_no_encontrado),
                modifier = Modifier.padding(padding).padding(Theme.dimens.spacingL)
            )

            else -> DetalleContent(
                reporte = reporte,
                uiState = uiState,
                onTomar = { viewModel.tomar(usuarioId) },
                onRespuesta = viewModel::setRespuesta,
                onResolver = viewModel::resolver,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun DetalleContent(
    reporte: ReporteItem,
    uiState: DetalleReporteUiState,
    onTomar: () -> Unit,
    onRespuesta: (String) -> Unit,
    onResolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            // imePadding antes del scroll para que el teclado no tape la respuesta
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(Theme.dimens.paddingSides)
            .padding(bottom = Theme.dimens.spacingXL),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
    ) {
        // ---------- reporte ----------
        SeccionDatos(titulo = stringResource(R.string.rep_seccion_reporte)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(reporte.motivo.labelRes), style = Theme.type.titleMedium, modifier = Modifier.weight(1f))
                StatusChip(label = stringResource(reporte.estado.labelRes), colores = reporte.estado.coloresCard)
            }
            if (reporte.atrasado) {
                StatusChip(
                    label = stringResource(R.string.rep_atrasado_dias, reporte.diasHabiles),
                    colores = coloresAtrasado,
                    modifier = Modifier.padding(vertical = Theme.dimens.spacingXXS)
                )
            }
            reporte.comentario?.let { FilaDato(stringResource(R.string.rep_campo_comentario), it) }
            FilaDato(stringResource(R.string.rep_campo_recibido), reporte.fechaCreacion.aTexto())
            FilaDato(stringResource(R.string.rep_campo_responsable), reporte.agente ?: stringResource(R.string.rep_sin_asignar))
            reporte.fechaToma?.let { FilaDato(stringResource(R.string.rep_campo_tomado), it.aTexto()) }
        }

        // ---------- compromiso y usuario ----------
        SeccionDatos(titulo = stringResource(R.string.rep_seccion_compromiso)) {
            FilaDato(stringResource(R.string.rep_campo_folio), reporte.folio)
            FilaDato(stringResource(R.string.rep_campo_empresa), reporte.empresa)
            FilaDato(
                stringResource(R.string.rep_campo_concepto),
                reporte.textoCuota?.let { "${reporte.concepto} · $it" } ?: reporte.concepto
            )
            FilaDato(stringResource(R.string.rep_campo_vencimiento), reporte.fechaVencimiento.aTexto())
            FilaDato(stringResource(R.string.rep_campo_total), reporte.total.aPesos(), destacado = true)
            FilaDato(stringResource(R.string.rep_campo_estado), stringResource(reporte.estadoCompromiso.labelRes))
        }
        SeccionDatos(titulo = stringResource(R.string.rep_seccion_usuario)) {
            FilaDato(stringResource(R.string.rep_campo_nombre), reporte.usuario)
            FilaDato(stringResource(R.string.rep_campo_correo), reporte.correo)
            reporte.telefono?.let { FilaDato(stringResource(R.string.rep_campo_telefono), it) }
        }

        // ---------- acciones según el estado (RN-23) ----------
        when (reporte.estado) {
            EstadoReporte.ABIERTO -> Button(onClick = onTomar, enabled = !uiState.guardando) {
                Text(stringResource(R.string.rep_tomar))
            }

            EstadoReporte.EN_GESTION -> {
                TextField(
                    value = uiState.respuesta,
                    onValueChange = onRespuesta,
                    label = stringResource(R.string.rep_respuesta_label),
                    placeholder = stringResource(R.string.rep_respuesta_placeholder),
                    singleLine = false
                )
                // el agente no anula, lo coordina con el ejecutivo
                Text(
                    stringResource(R.string.rep_coordinar, reporte.empresa),
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant
                )
                Button(onClick = onResolver, enabled = !uiState.guardando) {
                    Text(stringResource(R.string.rep_marcar_resuelto))
                }
            }

            EstadoReporte.RESUELTO -> Card(variant = ComponentVariant.Secondary, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Theme.dimens.spacingM)) {
                    Text(stringResource(R.string.rep_respuesta_enviada), style = Theme.type.labelLarge)
                    Text(reporte.respuesta.orEmpty(), style = Theme.type.bodyMedium)
                    Text(
                        stringResource(R.string.rep_resuelto_el, reporte.fechaResolucion?.aTexto().orEmpty()),
                        style = Theme.type.bodySmall,
                        color = Theme.colores.onSurfaceVariant
                    )
                }
            }
        }

        uiState.error?.let {
            Text(stringResource(it), style = Theme.type.bodyMedium, color = Theme.colores.error)
        }
    }
}
