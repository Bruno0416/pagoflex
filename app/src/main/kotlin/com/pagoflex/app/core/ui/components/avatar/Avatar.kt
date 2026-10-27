package com.pagoflex.app.core.ui.components.avatar

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.pagoflex.app.core.ui.theme.ButtonShape
import com.pagoflex.app.core.ui.theme.Dimens.avatarRadius
import com.pagoflex.app.core.ui.theme.Theme

// Avatar compartido: imagen opcional y fallback iniciales.
@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    fallback: String,
    colores: CardColors,
    imagen: ByteArray? = null,
    tamano: Dp = avatarRadius,
    circular: Boolean = false
) {
    val forma = if (circular) CircleShape else ButtonShape
    val bitmap = remember(imagen) {
        imagen?.let { bytes ->
            runCatching {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }.getOrNull()
        }
    }
    Surface(
        modifier = modifier.size(tamano),
        shape = forma,
        color = colores.containerColor,
        contentColor = colores.contentColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(forma)
                )
            } else {
                Text(fallback, style = Theme.type.headlineSmall)
            }
        }
    }
}
