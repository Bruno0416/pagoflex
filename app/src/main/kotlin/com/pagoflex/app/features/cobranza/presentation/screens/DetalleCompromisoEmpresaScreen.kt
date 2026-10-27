package com.pagoflex.app.features.cobranza.presentation.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.rules.MotivoAnulacion
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.chips.StatusChip
import com.pagoflex.app.core.ui.components.containers.FilaDato
import com.pagoflex.app.core.ui.components.containers.SeccionDatos
import com.pagoflex.app.core.ui.components.feedback.SnackbarHost
import com.pagoflex.app.core.ui.components.inputs.SelectField
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.components.overlays.BottomSheet
import com.pagoflex.app.core.ui.styles.coloresCard
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aPesos
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.presentation.model.CompromisoEmpresa
import com.pagoflex.app.features.cobranza.presentation.model.DetalleCompromisoUiState
import com.pagoflex.app.features.cobranza.presentation.viewmodel.DetalleCompromisoViewModel
import com.pagoflex.app.features.home.presentation.model.EstadoCompromiso

@Composable
fun DetalleCompromisoEmpresaScreen(
    db: LocalDatabase,
    usuarioId: Int,
    folio: String,
    onVolver: () -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { CobranzaRepository(db) }
    val viewModel: DetalleCompromisoViewModel = viewModel(
        key = "detalle-$folio",
        factory = viewModelFactory {
            initializer { DetalleCompromisoViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(folio) {
        viewModel.cargar(usuarioId, folio)
    }

    // mostrar el aviso de anulación una sola vez
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
            BarraSuperior(
                titulo = stringResource(R.string.cob_titulo_detalle),
                subtitulo = folio,
                onVolver = onVolver
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        val compromiso = uiState.compromiso

        when {
            uiState.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            compromiso == null -> Text(
                text = stringResource(uiState.error ?: R.string.cob_no_encontrado),
                modifier = Modifier.padding(padding).padding(Theme.dimens.spacingL)
            )

            else -> DetalleContent(
                compromiso = compromiso,
                uiState = uiState,
                onAnular = viewModel::abrirAnulacion,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (uiState.hojaAnulacionVisible) {
        HojaAnulacion(
            uiState = uiState,
            onMotivo = viewModel::setMotivo,
            onComentario = viewModel::setComentario,
            onConfirmar = { viewModel.confirmarAnulacion(usuarioId) },
            onCerrar = viewModel::cerrarAnulacion
        )
    }
}

@Composable
private fun DetalleContent(
    compromiso: CompromisoEmpresa,
    uiState: DetalleCompromisoUiState,
    onAnular: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Theme.dimens.paddingSides)
            .padding(bottom = Theme.dimens.spacingXL),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
    ) {
        // ---------- compromiso ----------
        SeccionDatos(titulo = stringResource(R.string.cob_seccion_compromiso)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(compromiso.concepto, style = Theme.type.titleMedium, modifier = Modifier.weight(1f))
                StatusChip(label = stringResource(compromiso.estado.labelRes), colores = compromiso.estado.coloresCard)
            }
            compromiso.textoCuota?.let { FilaDato(stringResource(R.string.cob_campo_cuota), it) }
            FilaDato(stringResource(R.string.cob_campo_vencimiento), compromiso.fechaVencimiento.aTexto())
            FilaDato(stringResource(R.string.cob_campo_monto), compromiso.monto.aPesos())
            if (compromiso.recargo > 0) {
                FilaDato(stringResource(R.string.cob_campo_recargo), compromiso.recargo.aPesos())
            }
            if (compromiso.estado == EstadoCompromiso.VENCIDO) {
                FilaDato(stringResource(R.string.cob_campo_dias_atraso), compromiso.diasAtraso.toString())
            }
            FilaDato(stringResource(R.string.cob_campo_total), compromiso.total.aPesos(), destacado = true)
        }

        // ---------- persona: solo nombre, rut y contacto (RN-22) ----------
        SeccionDatos(titulo = stringResource(R.string.cob_seccion_persona)) {
            FilaDato(stringResource(R.string.cob_campo_nombre), compromiso.persona.nombre)
            FilaDato(stringResource(R.string.cob_campo_rut), compromiso.persona.rut)
            FilaDato(stringResource(R.string.cob_campo_correo), compromiso.persona.correo)
            compromiso.persona.telefono?.let { FilaDato(stringResource(R.string.cob_campo_telefono), it) }
        }

        // ---------- pago ----------
        compromiso.pago?.let { pago ->
            SeccionDatos(titulo = stringResource(R.string.cob_seccion_pago)) {
                FilaDato(stringResource(R.string.cob_campo_comprobante), pago.comprobante)
                FilaDato(stringResource(R.string.cob_campo_fecha), pago.fecha.aTexto())
                FilaDato(stringResource(R.string.cob_campo_canal), pago.canal)
                FilaDato(stringResource(R.string.cob_campo_monto_pagado), pago.monto.aPesos())
            }
        }

        // ---------- reporte del usuario en modo lectura (RF-17) ----------
        uiState.reporte?.let { reporte ->
            SeccionDatos(titulo = stringResource(R.string.cob_seccion_reporte)) {
                FilaDato(stringResource(R.string.cob_campo_numero_reporte), reporte.numero)
                FilaDato(stringResource(R.string.cob_campo_motivo), stringResource(reporte.motivoRes))
                reporte.comentario?.let { FilaDato(stringResource(R.string.cob_campo_comentario), it) }
                FilaDato(stringResource(R.string.cob_campo_estado), stringResource(reporte.estadoRes))
                FilaDato(stringResource(R.string.cob_campo_fecha), reporte.fecha.aTexto())
            }
        }

        // ---------- trazabilidad (RNF-15) ----------
        SeccionDatos(titulo = stringResource(R.string.cob_seccion_registro)) {
            FilaDato(
                stringResource(R.string.cob_campo_registrado_por),
                compromiso.registradoPor ?: stringResource(R.string.cob_registrado_pagoflex)
            )
            FilaDato(stringResource(R.string.cob_campo_fecha_registro), compromiso.fechaRegistro.aTexto())
            compromiso.anulacion?.let { anulacion ->
                FilaDato(stringResource(R.string.cob_campo_anulado_por), anulacion.ejecutivo.orEmpty())
                FilaDato(stringResource(R.string.cob_campo_motivo), stringResource(anulacion.motivo.labelRes))
                anulacion.comentario?.let { FilaDato(stringResource(R.string.cob_campo_comentario), it) }
                FilaDato(stringResource(R.string.cob_campo_fecha_anulacion), anulacion.fecha.aTexto())
            }
        }

        // ---------- anular (RN-21) ----------
        when (compromiso.estado) {
            EstadoCompromiso.PENDIENTE, EstadoCompromiso.VENCIDO, EstadoCompromiso.EN_REVISION ->
                OutlinedButton(onClick = onAnular, variant = ComponentVariant.Destructive) {
                    Text(stringResource(R.string.cob_anular))
                }

            EstadoCompromiso.PAGADO -> Text(
                text = stringResource(R.string.cob_pagado_no_anulable),
                style = Theme.type.bodySmall,
                color = Theme.colores.onSurfaceVariant
            )

            EstadoCompromiso.ANULADO -> Unit
        }
    }
}

// hoja para anular con motivo y comentario (RN-21)
@Composable
private fun HojaAnulacion(
    uiState: DetalleCompromisoUiState,
    onMotivo: (MotivoAnulacion) -> Unit,
    onComentario: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCerrar: () -> Unit,
) {
    val opciones = MotivoAnulacion.entries.map { it to stringResource(it.labelRes) }

    BottomSheet(onDismissRequest = onCerrar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(Theme.dimens.spacingL),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
        ) {
            Text(
                stringResource(R.string.cob_anular),
                style = Theme.type.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                stringResource(R.string.cob_anular_aviso),
                style = Theme.type.bodyMedium,
                color = Theme.colores.onSurfaceVariant
            )

            SelectField(
                selectedOption = opciones.firstOrNull { it.first == uiState.motivo }?.second.orEmpty(),
                options = opciones.map { it.second },
                onOptionSelected = { texto -> opciones.firstOrNull { it.second == texto }?.let { onMotivo(it.first) } },
                label = stringResource(R.string.cob_motivo),
                placeholder = stringResource(R.string.cob_elige_motivo)
            )

            TextField(
                value = uiState.comentario,
                onValueChange = onComentario,
                label = stringResource(
                    if (uiState.motivo == MotivoAnulacion.OTRO) R.string.cob_comentario_obligatorio
                    else R.string.cob_comentario_opcional
                ),
                singleLine = false,
                error = uiState.errorAnulacion?.let { stringResource(it) }
            )

            Button(onClick = onConfirmar, variant = ComponentVariant.Destructive, enabled = !uiState.anulando) {
                Text(stringResource(R.string.cob_confirmar_anulacion))
            }
        }
    }
}
