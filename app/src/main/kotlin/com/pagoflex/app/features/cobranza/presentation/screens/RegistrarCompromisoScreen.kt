package com.pagoflex.app.features.cobranza.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.components.navigation.BarraSuperior
import com.pagoflex.app.core.ui.components.overlays.Dialog
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.utils.aPesos
import com.pagoflex.app.core.utils.aTexto
import com.pagoflex.app.features.cobranza.data.CobranzaRepository
import com.pagoflex.app.features.cobranza.presentation.model.CampoCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.RegistrarCompromisoUiState
import com.pagoflex.app.features.cobranza.presentation.viewmodel.RegistrarCompromisoViewModel

@Composable
fun RegistrarCompromisoScreen(
    db: LocalDatabase,
    usuarioId: Int,
    onVolverResumen: () -> Unit,
) {
    // ---------- dependencias ----------
    val repositorio = remember(db) { CobranzaRepository(db) }
    val viewModel: RegistrarCompromisoViewModel = viewModel(
        factory = viewModelFactory {
            initializer { RegistrarCompromisoViewModel(repositorio) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ---------- contenido ----------
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { BarraSuperior(titulo = stringResource(R.string.cob_titulo_registrar)) }
    ) { padding ->
        FormularioRegistro(
            uiState = uiState,
            onCampo = viewModel::setCampo,
            onTotalCuotas = viewModel::setTotalCuotas,
            onSalirCampo = viewModel::validarCampo,
            onSalirTotal = viewModel::validarTotalCuotas,
            onRegistrar = { viewModel.registrar(usuarioId) },
            modifier = Modifier.padding(padding)
        )
    }

    // confirmación con el folio nuevo
    uiState.registrado?.let { compromiso ->
        Dialog(
            onDismissRequest = viewModel::nuevoRegistro,
            title = stringResource(R.string.cob_registrado_titulo),
            text = stringResource(
                R.string.cob_registrado_texto,
                compromiso.folio,
                compromiso.monto.toLong().aPesos(),
                compromiso.fechaVencimiento.aTexto()
            ),
            confirmText = stringResource(R.string.cob_volver_resumen),
            onConfirm = {
                viewModel.nuevoRegistro()
                onVolverResumen()
            },
            dismissText = stringResource(R.string.cob_registrar_otro),
            onDismiss = viewModel::nuevoRegistro
        )
    }
}

@Composable
private fun FormularioRegistro(
    uiState: RegistrarCompromisoUiState,
    onCampo: (CampoCompromiso, String) -> Unit,
    onTotalCuotas: (String) -> Unit,
    onSalirCampo: (CampoCompromiso) -> Unit,
    onSalirTotal: () -> Unit,
    onRegistrar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val f = uiState.formulario
    val e = uiState.errores
    val numerico = KeyboardOptions(keyboardType = KeyboardType.Number)

    @Composable
    fun error(campo: CampoCompromiso): String? = e[campo]?.let { stringResource(it) }

    Column(
        modifier = modifier
            .fillMaxSize()
            // imePadding antes del scroll para que el teclado no tape los campos
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(Theme.dimens.paddingSides)
            .padding(bottom = Theme.dimens.spacingXL),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
    ) {
        // ---------- persona ----------
        Titulo(stringResource(R.string.cob_seccion_persona))
        TextField(
            value = f.rut,
            onValueChange = { onCampo(CampoCompromiso.RUT, it) },
            onUnfocus = { onSalirCampo(CampoCompromiso.RUT) },
            label = stringResource(R.string.cob_rut),
            placeholder = stringResource(R.string.cob_ph_rut),
            error = error(CampoCompromiso.RUT)
        )

        uiState.personaEncontrada?.let {
            Card(variant = ComponentVariant.Secondary) {
                Text(
                    text = stringResource(R.string.cob_persona_encontrada, it.nombre, it.correo),
                    style = Theme.type.bodyMedium,
                    modifier = Modifier.padding(Theme.dimens.spacingM)
                )
            }
        }

        // persona nueva: pedir datos básicos (RN-20)
        if (uiState.pedirDatosPersona) {
            Text(
                stringResource(R.string.cob_persona_nueva),
                style = Theme.type.bodySmall,
                color = Theme.colores.onSurfaceVariant
            )
            TextField(f.nombres, { onCampo(CampoCompromiso.NOMBRES, it) }, onUnfocus = { onSalirCampo(CampoCompromiso.NOMBRES) }, label = stringResource(R.string.cob_nombres), error = error(CampoCompromiso.NOMBRES))
            TextField(f.apellidos, { onCampo(CampoCompromiso.APELLIDOS, it) }, onUnfocus = { onSalirCampo(CampoCompromiso.APELLIDOS) }, label = stringResource(R.string.cob_apellidos), error = error(CampoCompromiso.APELLIDOS))
            TextField(
                f.correo, { onCampo(CampoCompromiso.CORREO, it) },
                onUnfocus = { onSalirCampo(CampoCompromiso.CORREO) },
                label = stringResource(R.string.cob_correo),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                error = error(CampoCompromiso.CORREO)
            )
            TextField(
                f.telefono, { onCampo(CampoCompromiso.TELEFONO, it) },
                onUnfocus = { onSalirCampo(CampoCompromiso.TELEFONO) },
                label = stringResource(R.string.cob_telefono),
                placeholder = stringResource(R.string.cob_ph_telefono),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                error = error(CampoCompromiso.TELEFONO)
            )
        }

        // ---------- compromiso ----------
        Titulo(stringResource(R.string.cob_seccion_compromiso))
        TextField(
            f.concepto, { onCampo(CampoCompromiso.CONCEPTO, it) },
            onUnfocus = { onSalirCampo(CampoCompromiso.CONCEPTO) },
            label = stringResource(R.string.cob_concepto),
            placeholder = stringResource(R.string.cob_ph_concepto),
            error = error(CampoCompromiso.CONCEPTO)
        )
        TextField(
            f.monto, { onCampo(CampoCompromiso.MONTO, it) },
            onUnfocus = { onSalirCampo(CampoCompromiso.MONTO) },
            label = stringResource(R.string.cob_monto),
            placeholder = stringResource(R.string.cob_ph_monto),
            keyboardOptions = numerico,
            error = error(CampoCompromiso.MONTO)
        )
        TextField(
            f.vencimiento, { onCampo(CampoCompromiso.VENCIMIENTO, it) },
            onUnfocus = { onSalirCampo(CampoCompromiso.VENCIMIENTO) },
            label = stringResource(R.string.cob_vencimiento),
            placeholder = stringResource(R.string.cob_ph_vencimiento),
            error = error(CampoCompromiso.VENCIMIENTO)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)) {
            TextField(
                f.numeroCuota, { onCampo(CampoCompromiso.CUOTA, it) },
                onUnfocus = { onSalirCampo(CampoCompromiso.CUOTA) },
                label = stringResource(R.string.cob_cuota),
                placeholder = stringResource(R.string.cob_ph_cuota),
                keyboardOptions = numerico,
                modifier = Modifier.weight(1f)
            )
            TextField(
                f.totalCuotas, onTotalCuotas,
                onUnfocus = onSalirTotal,
                label = stringResource(R.string.cob_total_cuotas),
                placeholder = stringResource(R.string.cob_ph_total),
                keyboardOptions = numerico,
                modifier = Modifier.weight(1f)
            )
        }
        error(CampoCompromiso.CUOTA)?.let { Text(it, style = Theme.type.bodySmall, color = Theme.colores.error) }
        Text(
            stringResource(R.string.cob_cuota_opcional),
            style = Theme.type.bodySmall,
            color = Theme.colores.onSurfaceVariant
        )

        // error general, por ejemplo duplicado
        uiState.errorGeneral?.let {
            Card(variant = ComponentVariant.Destructive) {
                Text(stringResource(it), style = Theme.type.bodyMedium, modifier = Modifier.padding(Theme.dimens.spacingM))
            }
        }

        Button(onClick = onRegistrar, enabled = !uiState.cargando) {
            Text(stringResource(R.string.cob_registrar_boton))
        }
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(texto, style = Theme.type.titleMedium.copy(fontWeight = FontWeight.Bold))
}
