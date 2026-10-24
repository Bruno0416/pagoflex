package com.pagoflex.app.features.home.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier,
    nombreUsuario: String,
    ultimaActualizacion: LocalDateTime,
    porcentajeCompletado: Double?,
) {
    val estadoCumplimiento = porcentajeCompletado?.let { porcentaje ->
        val categoria = when {
            porcentaje >= 95.0 -> stringResource(R.string.home_cumplimiento_ejemplar)
            porcentaje >= 80.0 -> stringResource(R.string.home_cumplimiento_al_dia)
            else -> stringResource(R.string.home_cumplimiento_en_camino)
        }
        stringResource(
            R.string.home_cumplimiento_porcentaje,
            String.format(Locale.forLanguageTag("es-CL"), "%.1f", porcentaje),
            categoria
        )
    } ?: stringResource(R.string.home_cumplimiento_historial_parcial)

    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ---------- saludo ----------
                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = stringResource(R.string.home_saludo, nombreUsuario),
                        style = Theme.type.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.size(Theme.dimens.spacingXS))

                    // ---------- estado de conexion ----------
                    EstadoConexion(
                        ultimaActualizacion = ultimaActualizacion
                    )
                }

                Spacer(Modifier.size(Theme.dimens.spacingS))

                // ---------- indicador de cumplimiento ----------
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
                            text = estadoCumplimiento,
                            style = Theme.type.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                    }
                }
            }
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Theme.colores.background
        )
    )
}


// ---------- previews ----------

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
