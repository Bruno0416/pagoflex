package com.pagoflex.app.features.home.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pagoflex.app.core.ui.components.containers.Card
import com.pagoflex.app.core.ui.styles.ChipColors
import com.pagoflex.app.core.ui.theme.ButtonShape
import com.pagoflex.app.core.ui.theme.Dimens.avatarRadius
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    // TODO: agregar soporte para imágenes
    fallback: String,
    colores: CardColors
) {

    Card(
        modifier = modifier
            .width(avatarRadius)
            .height(avatarRadius),
        colors = colores,
        shape = ButtonShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = fallback,
                style = Theme.type.headlineSmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun AvatarPreview() {
    PagoFlexTheme {
        Surface {
            Avatar(
                fallback = "AB",
                colores = CardDefaults.cardColors(
                    containerColor = ChipColors.pendienteContainer,
                    contentColor = ChipColors.pendienteContent,
                )
            )

        }
    }
}