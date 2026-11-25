package com.pagoflex.app.features.perfil.presentation.screens

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pagoflex.app.R
import com.pagoflex.app.core.platform.Permisos
import com.pagoflex.app.core.ui.components.avatar.Avatar
import com.pagoflex.app.core.ui.components.buttons.Button
import com.pagoflex.app.core.ui.components.buttons.IconButton
import com.pagoflex.app.core.ui.components.buttons.OutlinedButton
import com.pagoflex.app.core.ui.components.buttons.TextButton
import com.pagoflex.app.core.ui.components.feedback.CircularProgressIndicator
import com.pagoflex.app.core.ui.components.feedback.SnackbarHost
import com.pagoflex.app.core.ui.components.inputs.TextField
import com.pagoflex.app.core.ui.components.model.ComponentSize
import com.pagoflex.app.core.ui.components.model.ComponentVariant
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.perfil.data.PerfilRepository
import com.pagoflex.app.features.perfil.presentation.model.PerfilUiState
import com.pagoflex.app.features.perfil.presentation.viewmodel.PerfilViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    onCambiarContrasena: () -> Unit
) {
    // ---------- estado y dependencias ----------
    val repositorio = remember { PerfilRepository() }
    val viewModel: PerfilViewModel = viewModel(
        factory = viewModelFactory { initializer { PerfilViewModel(repositorio) } }
    )
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val errorCamara = stringResource(R.string.perfil_camara_no_disponible)
    val errorGaleria = stringResource(R.string.perfil_galeria_error)
    val permisoDenegado = stringResource(R.string.perfil_permiso_camara_denegado)
    val accionAjustes = stringResource(R.string.perfil_abrir_ajustes)

    // ---------- camara y permisos ----------
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { imagen: Bitmap? ->
        if (imagen != null) viewModel.guardarAvatar(imagen)
    }

    // --- abrir camara ---
    fun abrirCamara() {
        // 1. iniciar la captura con el permiso concedido
        try {
            camara.launch(null)
        } catch (_: RuntimeException) {
            scope.launch { snackbar.showSnackbar(errorCamara) }
        }
    }

    // 1. solicitar el permiso de camara
    val solicitarCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            scope.launch {
                // 1. informar el rechazo y ofrecer acceso a los ajustes
                val resultado = snackbar.showSnackbar(
                    message = permisoDenegado,
                    actionLabel = accionAjustes,
                    duration = SnackbarDuration.Long
                )
                if (resultado == SnackbarResult.ActionPerformed) {
                    try {
                        Permisos.abrirAjustes(context)
                    } catch (_: ActivityNotFoundException) {
                        snackbar.showSnackbar(errorCamara)
                    }
                }
            }
        }
    }

    // ---------- galeria ----------
    // 1. elegir una imagen sin permiso de almacenamiento
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                // 1. decodificar
                val bitmap = withContext(Dispatchers.IO) {
                    runCatching {
                        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        context.contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it, null, opciones)
                        }
                        if (opciones.outWidth <= 0 || opciones.outHeight <= 0) return@runCatching null
                        var muestra = 1
                        while (maxOf(opciones.outWidth, opciones.outHeight) / muestra > 512) muestra *= 2
                        val reducidas = BitmapFactory.Options().apply { inSampleSize = muestra }
                        context.contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it, null, reducidas)
                        }
                    }.getOrNull()
                }
                if (bitmap != null) viewModel.guardarAvatar(bitmap)
                else snackbar.showSnackbar(errorGaleria)
            }
        }
    }

    // ---------- mensajes de operacion ----------
    val mensaje = estado.mensaje?.let { stringResource(it) }
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            snackbar.showSnackbar(mensaje)
            viewModel.limpiarMensaje()
        }
    }

    PerfilContent(
        state = estado,
        snackbar = snackbar,
        onVolver = onVolver,
        onCerrarSesion = onCerrarSesion,
        onTomarFoto = {
            // 1. verificar permiso antes de usar la camara
            if (Permisos.tieneCamara(context)) abrirCamara()
            else solicitarCamara.launch(Permisos.CAMARA)
        },
        onElegirFoto = {
            try {
                galeria.launch("image/*")
            } catch (_: RuntimeException) {
                scope.launch { snackbar.showSnackbar(errorGaleria) }
            }
        },
        onEliminarFoto = viewModel::eliminarAvatar,
        onEditar = viewModel::iniciarEdicion,
        onCancelar = viewModel::cancelarEdicion,
        onNombres = viewModel::setNombres,
        onApellidos = viewModel::setApellidos,
        onCorreo = viewModel::setCorreo,
        onLimpiarErrorContrasena = viewModel::limpiarErrorContrasena,
        onAbrirCambioContrasena = {
            if (!viewModel.uiState.value.cambiandoContrasena) {
                viewModel.abrirCambioContrasena()
                if (viewModel.uiState.value.cambiandoContrasena) onCambiarContrasena()
            }
        },
        onGuardar = viewModel::guardarCambios,
        onReintentar = viewModel::cargarPerfil
    )
}

@Composable
private fun PerfilContent(
    state: PerfilUiState,
    snackbar: SnackbarHostState,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    onTomarFoto: () -> Unit,
    onElegirFoto: () -> Unit,
    onEliminarFoto: () -> Unit,
    onEditar: () -> Unit,
    onCancelar: () -> Unit,
    onNombres: (String) -> Unit,
    onApellidos: (String) -> Unit,
    onCorreo: (String) -> Unit,
    onLimpiarErrorContrasena: () -> Unit,
    onAbrirCambioContrasena: () -> Unit,
    onGuardar: (String) -> Unit,
    onReintentar: () -> Unit
) {
    val modoOscuro = isSystemInDarkTheme()
    val colorCampo = if (modoOscuro) Theme.colores.surfaceContainerLow else Theme.colores.surfaceContainerHigh
    val bordeCampo = Theme.colores.outlineVariant
    val datoNoDisponible = stringResource(R.string.perfil_dato_no_disponible)
    val nombreGenerico = stringResource(R.string.home_usuario_generico)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.perfil_titulo), style = Theme.type.titleLarge) },
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
        when {
            state.cargando -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.perfil == null -> Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(Theme.dimens.paddingSides),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(state.errorGeneral ?: R.string.perfil_no_disponible))
                Button(onClick = onReintentar) { Text(stringResource(R.string.perfil_reintentar)) }
            }

            else -> {
                val perfil = checkNotNull(state.perfil)
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(Theme.dimens.paddingSides),
                    verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
                ) {
                    // ---------- fotografia ----------
                    Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = Theme.dimens.spacingL),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)
                        ) {
                            Avatar(
                                fallback = iniciales(perfil.nombres, perfil.apellidos),
                                colores = CardDefaults.cardColors(
                                    containerColor = Theme.colores.primaryContainer,
                                    contentColor = Theme.colores.onPrimaryContainer
                                ),
                                imagen = state.avatar,
                                circular = true,
                                tamano = 96.dp
                            )
                            Text(
                                text = "${perfil.nombres} ${perfil.apellidos}".trim().ifBlank { nombreGenerico },
                                style = Theme.type.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = onTomarFoto,
                                    variant = ComponentVariant.Secondary,
                                    enabled = !state.guardando && !state.editando,
                                    size = ComponentSize.Small
                                ) { Text(stringResource(R.string.perfil_cambiar_foto)) }
                                OutlinedButton(
                                    onClick = onElegirFoto,
                                    enabled = !state.guardando && !state.editando,
                                    size = ComponentSize.Small,
                                    variant = ComponentVariant.Secondary
                                ) { Text(stringResource(R.string.perfil_elegir_galeria)) }
                            }
                            if (!perfil.urlAvatar.isNullOrBlank()) {
                                TextButton(
                                    onClick = onEliminarFoto,
                                    enabled = !state.guardando && !state.editando,
                                    size = ComponentSize.Small,
                                    variant = ComponentVariant.Destructive
                                ) { Text(stringResource(R.string.perfil_eliminar_foto)) }
                            }
                    }

                    // ---------- datos personales ----------
                    Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    stringResource(R.string.perfil_datos_personales),
                                    modifier = Modifier.weight(1f),
                                    style = Theme.type.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                if (!state.editando) {
                                    Spacer(Modifier.width(Theme.dimens.spacingXS))
                                    TextButton(
                                        onClick = onEditar,
                                        enabled = !state.guardando,
                                        size = ComponentSize.Small
                                    ) { Text(stringResource(R.string.perfil_editar)) }
                                }
                            }
                            if (state.editando) {
                                // 1. mantener la contrasena en memoria solo mientras se edita
                                var contrasena by remember { mutableStateOf("") }
                                LaunchedEffect(state.cambioCorreo) {
                                    if (!state.cambioCorreo) contrasena = ""
                                }
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.nombres,
                                    enabled = !state.guardando,
                                    onValueChange = onNombres,
                                    label = stringResource(R.string.perfil_nombres),
                                    containerColor = colorCampo,
                                    outlineColor = bordeCampo,
                                    error = state.errorNombres?.let { stringResource(it) }
                                )
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.apellidos,
                                    enabled = !state.guardando,
                                    onValueChange = onApellidos,
                                    label = stringResource(R.string.perfil_apellidos),
                                    containerColor = colorCampo,
                                    outlineColor = bordeCampo,
                                    error = state.errorApellidos?.let { stringResource(it) }
                                )
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.correo,
                                    enabled = !state.guardando,
                                    onValueChange = onCorreo,
                                    label = stringResource(R.string.perfil_correo),
                                    containerColor = colorCampo,
                                    outlineColor = bordeCampo,
                                    error = state.errorCorreo?.let { stringResource(it) }
                                )
                                if (state.cambioCorreo) {
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = contrasena,
                                        enabled = !state.guardando,
                                        onValueChange = { contrasena = it; onLimpiarErrorContrasena() },
                                        label = stringResource(R.string.perfil_contrasena_actual),
                                        containerColor = colorCampo,
                                        outlineColor = bordeCampo,
                                        isPassword = true,
                                        error = state.errorContrasena?.let { stringResource(it) }
                                    )
                                    Text(
                                        stringResource(R.string.perfil_aviso_correo),
                                        style = Theme.type.bodySmall,
                                        color = Theme.colores.onSurfaceVariant
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingS)) {
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        enabled = !state.guardando,
                                        size = ComponentSize.Small,
                                        variant = ComponentVariant.Secondary,
                                        onClick = onCancelar
                                    ) { Text(stringResource(R.string.perfil_cancelar)) }
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        enabled = !state.guardando,
                                        size = ComponentSize.Small,
                                        onClick = { onGuardar(contrasena) }
                                    ) { Text(stringResource(R.string.perfil_guardar)) }
                                }
                            } else {
                                DatoPerfil(stringResource(R.string.perfil_nombres), perfil.nombres.ifBlank { datoNoDisponible })
                                DatoPerfil(stringResource(R.string.perfil_apellidos), perfil.apellidos.ifBlank { datoNoDisponible })
                                DatoPerfil(stringResource(R.string.perfil_correo), perfil.correo.ifBlank { datoNoDisponible })
                            }
                            DatoPerfil(
                                stringResource(R.string.perfil_rut),
                                ocultarRut(perfil.rut)
                            )
                    }

                    Spacer(Modifier.size(Theme.dimens.spacingXS))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(Theme.dimens.radiusS))
                            .clickable(enabled = !state.guardando && !state.editando, onClick = onAbrirCambioContrasena)
                            .padding(horizontal = Theme.dimens.spacingXS, vertical = Theme.dimens.spacingM),
                        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXXS)) {
                            Text(stringResource(R.string.perfil_cambiar_contrasena), style = Theme.type.bodyLarge)
                            Text(stringResource(R.string.perfil_seguridad_descripcion),
                                style = Theme.type.bodySmall, color = Theme.colores.onSurfaceVariant)
                        }
                        Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null,
                            tint = Theme.colores.onSurfaceVariant)
                    }

                    if (state.guardando) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    state.errorGeneral?.let {
                        Text(stringResource(it), color = Theme.colores.error, style = Theme.type.bodyMedium)
                    }

                    // ---------- sesion ----------
                    Spacer(Modifier.size(Theme.dimens.spacingS))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onCerrarSesion,
                        enabled = !state.guardando,
                        variant = ComponentVariant.Destructive
                    ) { Text(stringResource(R.string.perfil_cerrar_sesion)) }
                    Spacer(Modifier.size(Theme.dimens.spacingL))
                }
            }
        }
    }
}

// ---------- helpers ----------

@Composable
private fun DatoPerfil(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Theme.dimens.spacingXS),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.spacingM),
        verticalAlignment = Alignment.Top
    ) {
        Text(titulo, modifier = Modifier.weight(0.4f), style = Theme.type.bodyMedium,
            color = Theme.colores.onSurfaceVariant)
        Text(valor, modifier = Modifier.weight(0.6f), style = Theme.type.bodyMedium,
            textAlign = TextAlign.End)
    }
}

private fun ocultarRut(rut: String): String {
    val limpio = rut.replace(".", "").replace("-", "").trim()
    return if (limpio.length >= 3) "${limpio.take(2)}.*.*-${limpio.last()}" else "**-?"
}

private fun iniciales(nombres: String, apellidos: String): String =
    (nombres.trim().take(1) + apellidos.trim().take(1)).uppercase().ifBlank { "?" }
