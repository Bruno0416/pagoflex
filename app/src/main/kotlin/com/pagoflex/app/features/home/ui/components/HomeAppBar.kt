
package com.pagoflex.app.features.home.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.pagoflex.app.R

@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier,
    nombreUsuario: String,
    ultimaActualizacion: LocalDateTime,
    porcentajeCompletado: Double
) {
    // 1. formatear fecha
    val fecha = ultimaActualizacion.format(
        DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm")
    )

    // 2. formatear porcentaje
    val porcentaje = String.format(
        Locale.forLanguageTag("es-CL"),
        "%.1f",
        porcentajeCompletado
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ---- saludo y actualización ----
        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = "Hola, $nombreUsuario",
                style = Theme.type.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.size(Theme.dimens.spacingXS))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = null,
                    tint = Theme.colores.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.size(Theme.dimens.spacingXS))

                Text(
                    text = fecha,
                    style = Theme.type.bodySmall,
                    color = Theme.colores.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.size(Theme.dimens.spacingS))

        // ---- indicador de cumplimiento ----
        Surface(
            shape = RoundedCornerShape(50),
            color = Theme.colores.primaryContainer,
            contentColor = Theme.colores.onPrimaryContainer
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Theme.dimens.spacingS,
                    vertical = Theme.dimens.spacingXS
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_workspace_premium),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.size(Theme.dimens.spacingXS))

                Text(
                    text = "$porcentaje% · Al día",
                    style = Theme.type.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

            }
        }
    }
}


// ----------------- PREVIEWS -----------------

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun HomeAppBarPreview() {
    PagoFlexTheme {
        Surface {
            HomeAppBar(
                modifier = Modifier.padding(
                    Theme.dimens.paddingSides
                ),
                nombreUsuario = "Camila",
                ultimaActualizacion = LocalDateTime.of(
                    2026, 10, 6, 8, 12
                ),
                porcentajeCompletado = 91.7
            )
        }
    }
}
