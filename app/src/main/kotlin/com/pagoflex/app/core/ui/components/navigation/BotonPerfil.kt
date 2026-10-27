package com.pagoflex.app.core.ui.components.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.avatar.Avatar
import com.pagoflex.app.core.ui.theme.Theme

// avatar de la barra superior que abre el perfil, igual que en el home
@Composable
fun BotonPerfil(
    iniciales: String,
    imagen: ByteArray?,
    onVerPerfil: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val descripcion = stringResource(R.string.perfil_abrir)
    Surface(
        onClick = onVerPerfil,
        modifier = modifier
            .padding(end = Theme.dimens.spacingS)
            .semantics { contentDescription = descripcion },
        shape = CircleShape,
        color = Color.Transparent,
    ) {
        Avatar(
            fallback = iniciales,
            imagen = imagen,
            tamano = 44.dp,
            circular = true,
            colores = CardDefaults.cardColors(
                containerColor = Theme.colores.primaryContainer,
                contentColor = Theme.colores.onPrimaryContainer
            )
        )
    }
}
