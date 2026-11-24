package com.pagoflex.app.features.auth.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.auth.presentation.validators.AuthValidators

@Composable
fun RequisitosContrasena(contrasena: String, modifier: Modifier = Modifier) {
    val requisitos = AuthValidators.requisitosContrasena(contrasena)

    Column(
        modifier = modifier.padding(start = Theme.dimens.spacingS),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.spacingXXS)
    ) {
        RequisitoContrasena(R.string.auth_requisito_longitud, requisitos.longitud)
        RequisitoContrasena(R.string.auth_requisito_mayuscula, requisitos.mayuscula)
        RequisitoContrasena(R.string.auth_requisito_minuscula, requisitos.minuscula)
        RequisitoContrasena(R.string.auth_requisito_numero, requisitos.numero)
        RequisitoContrasena(R.string.auth_requisito_simbolo, requisitos.simbolo)
    }
}

@Composable
private fun RequisitoContrasena(@StringRes textoRes: Int, cumplido: Boolean) {
    val color = if (cumplido) Theme.colores.primary
        else Theme.colores.onSurfaceVariant.copy(alpha = 0.55f)
    val estado = stringResource(
        if (cumplido) R.string.auth_requisito_cumplido else R.string.auth_requisito_pendiente
    )

    Row(
        modifier = Modifier.semantics(mergeDescendants = true) { stateDescription = estado },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(if (cumplido) R.drawable.ic_check else R.drawable.ic_error),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color
        )
        Text(
            text = stringResource(textoRes),
            style = Theme.type.bodySmall,
            color = color
        )
    }
}
