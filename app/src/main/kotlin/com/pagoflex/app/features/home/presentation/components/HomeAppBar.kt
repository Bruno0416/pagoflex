package com.pagoflex.app.features.home.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.components.avatar.Avatar
import com.pagoflex.app.features.cumplimiento.data.CategoriaCumplimiento
import com.pagoflex.app.features.cumplimiento.data.ResultadoCumplimiento
import com.pagoflex.app.core.ui.theme.PagoFlexTheme
import com.pagoflex.app.core.ui.theme.Theme
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier,
    nombreUsuario: String,
    inicialesUsuario: String = "",
    avatarUsuario: ByteArray? = null,
    onVerPerfil: () -> Unit,
    ultimaActualizacion: LocalDateTime,
    cumplimiento: ResultadoCumplimiento,
    onVerCumplimiento: () -> Unit,
) {
    val estadoCumplimiento = cumplimiento.porcentaje?.let { porcentaje ->
        val categoria = when (cumplimiento.categoria) {
            CategoriaCumplimiento.EJEMPLAR -> stringResource(R.string.home_cumplimiento_ejemplar)
            CategoriaCumplimiento.AL_DIA -> stringResource(R.string.home_cumplimiento_al_dia)
            CategoriaCumplimiento.EN_CAMINO -> stringResource(R.string.home_cumplimiento_en_camino)
            null -> stringResource(R.string.home_cumplimiento_historial_parcial)
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
                // ---------- acceso al perfil ----------
                val descripcionPerfil = stringResource(R.string.perfil_abrir)
                Surface(
                    onClick = onVerPerfil,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        .semantics { contentDescription = descripcionPerfil },
                    shape = RoundedCornerShape(Theme.dimens.radiusXS),
                    color = Color.Transparent
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(
                            fallback = inicialesUsuario.ifBlank { nombreUsuario.take(1).uppercase() },
                            imagen = avatarUsuario,
                            tamano = 44.dp,
                            circular = true,
                            colores = CardDefaults.cardColors(
                                containerColor = Theme.colores.primaryContainer,
                                contentColor = Theme.colores.onPrimaryContainer
                            )
                        )
                        Spacer(Modifier.size(Theme.dimens.spacingS))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nombreUsuario,
                                style = Theme.type.titleLarge.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.size(Theme.dimens.spacingXXS))
                            EstadoConexion(ultimaActualizacion = ultimaActualizacion)
                        }
                    }
                }

                Spacer(Modifier.size(Theme.dimens.spacingS))

                // ---------- indicador de cumplimiento ----------
                Surface(
                    onClick = onVerCumplimiento,
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
                cumplimiento = ResultadoCumplimiento(36, 33, 91.7, CategoriaCumplimiento.AL_DIA),
                onVerCumplimiento = {},
                onVerPerfil = {}
            )
        }
    }
}
