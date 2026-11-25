package com.pagoflex.app.features.perfil.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.components.RequisitosContrasena
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators
import com.pagoflex.app.features.perfil.data.PerfilRepository
import com.pagoflex.app.features.perfil.presentation.model.PerfilUiState
import com.pagoflex.app.features.perfil.presentation.viewmodel.PerfilViewModel

@Composable
internal fun CambiarContrasenaScreen(
    perfilOwner: ViewModelStoreOwner,
    onVolver: () -> Unit
) {
    val repositorio = remember { PerfilRepository() }
    val viewModel: PerfilViewModel = viewModel(
        viewModelStoreOwner = perfilOwner,
        factory = viewModelFactory { initializer { PerfilViewModel(repositorio) } }
    )
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Volver tras guardar o cancelar; el mensaje de exito se conserva en Perfil.
    LaunchedEffect(estado.cambiandoContrasena, estado.guardando) {
        if (!estado.cambiandoContrasena && !estado.guardando) onVolver()
    }
    BackHandler { viewModel.cerrarCambioContrasena() }

    CambiarContrasenaContent(
        state = estado,
        onVolver = viewModel::cerrarCambioContrasena,
        onEditarActual = viewModel::limpiarErrorContrasenaActual,
        onEditarNueva = viewModel::limpiarErrorNuevaContrasena,
        onEditarConfirmacion = viewModel::limpiarErrorConfirmacion,
        onGuardar = viewModel::cambiarContrasena
    )
}

@Composable
private fun CambiarContrasenaContent(
    state: PerfilUiState,
    onVolver: () -> Unit,
    onEditarActual: () -> Unit,
    onEditarNueva: () -> Unit,
    onEditarConfirmacion: () -> Unit,
    onGuardar: (String, String, String) -> Unit
) {
    // Mantener las claves solo en memoria mientras el formulario esta visible.
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    val focus = LocalFocusManager.current
    val errorConfirmacion = state.errorConfirmacion ?: if (confirmacion.isNotEmpty()) {
        AuthValidators.validarConfirmarContrasena(nueva, confirmacion)
    } else null

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.perfil_cambiar_contrasena), style = Theme.type.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver,
                        enabled = !state.guardando,
                        variant = ComponentVariant.Secondary,
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(R.string.perfil_volver)
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Theme.colores.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding()
                .padding(Theme.dimens.paddingSides)
                .padding(bottom = Theme.dimens.spacingL)
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = Theme.dimens.spacingM, bottom = Theme.dimens.spacingS),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = actual,
                    onValueChange = { actual = it; onEditarActual() },
                    label = stringResource(R.string.perfil_contrasena_actual),
                    leadingIcon = { Icon(painterResource(R.drawable.ic_lock), contentDescription = null) },
                    isPassword = true,
                    enabled = !state.guardando,
                    error = state.errorContrasena?.let { stringResource(it) }
                )
                Spacer(Modifier.height(Theme.dimens.spacingM))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = nueva,
                    onValueChange = { nueva = it; onEditarNueva() },
                    label = stringResource(R.string.auth_recuperacion_nueva_contrasena),
                    leadingIcon = { Icon(painterResource(R.drawable.ic_lock), contentDescription = null) },
                    isPassword = true,
                    enabled = !state.guardando,
                    error = state.errorNuevaContrasena?.let { stringResource(it) }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmacion,
                    onValueChange = { confirmacion = it; onEditarConfirmacion() },
                    label = stringResource(R.string.auth_registro_confirmar_contrasena),
                    leadingIcon = { Icon(painterResource(R.drawable.ic_lock), contentDescription = null) },
                    isPassword = true,
                    enabled = !state.guardando,
                    error = errorConfirmacion?.let { stringResource(it) }
                )
                RequisitosContrasena(nueva)
                state.errorGeneral?.let {
                    Text(stringResource(it), style = Theme.type.bodyMedium, color = Theme.colores.error)
                }
            }
            Spacer(Modifier.height(Theme.dimens.spacingM))
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.guardando,
                onClick = {
                    focus.clearFocus()
                    onGuardar(actual, nueva, confirmacion)
                }
            ) {
                if (state.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp, color = Theme.colores.onSurfaceVariant)
                } else {
                    Text(stringResource(R.string.perfil_guardar_contrasena))
                }
            }
        }
    }
}
