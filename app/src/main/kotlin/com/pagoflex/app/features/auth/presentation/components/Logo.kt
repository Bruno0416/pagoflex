package com.pagoflex.app.features.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.core.ui.components.avatar.Avatar

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Avatar(
            fallback = "P",
            colores = CardDefaults.cardColors(
                contentColor = Theme.colores.onPrimary,
                containerColor = Theme.colores.primary
            )
        )

        Spacer(Modifier.height(Theme.dimens.spacingS))

        Text(
            text = stringResource(R.string.app_name),
            style = Theme.type.titleMedium
        )
    }
}

