package com.pagoflex.app.features.reportes.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme
import com.pagoflex.app.features.reportes.data.remote.ReportesRemoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// el agente revisa la foto del comprobante para confirmar si el pago se hizo
@Composable
fun ComprobanteDialog(numero: String, onCerrar: () -> Unit) {
    val remoto = remember { ReportesRemoto.crear() }
    var imagen by remember { mutableStateOf<ImageBitmap?>(null) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(numero) {
        val bytes = runCatching { remoto.comprobante(numero) }.getOrNull()
        imagen = bytes?.let {
            withContext(Dispatchers.Default) { BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap() }
        }
        error = imagen == null
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = { TextButton(onClick = onCerrar) { Text(stringResource(R.string.rep_comprobante_cerrar)) } },
        title = { Text(stringResource(R.string.rep_comprobante_titulo)) },
        text = {
            Box(Modifier.fillMaxWidth().heightIn(min = 120.dp), contentAlignment = Alignment.Center) {
                val foto = imagen
                when {
                    foto != null -> Image(
                        bitmap = foto,
                        contentDescription = stringResource(R.string.rep_comprobante_titulo),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp)
                    )

                    error -> Text(stringResource(R.string.rep_comprobante_error), color = Theme.colores.error)
                    else -> CircularProgressIndicator()
                }
            }
        }
    )
}
