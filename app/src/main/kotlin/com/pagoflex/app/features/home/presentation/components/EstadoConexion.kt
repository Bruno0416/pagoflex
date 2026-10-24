package com.pagoflex.app.features.home.presentation.components

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDateTime

@Composable
fun EstadoConexion(
    ultimaActualizacion: LocalDateTime?
) {
    val contexto = LocalContext.current

    // ---------- conectividad ----------
    val gestor = remember(contexto) {
        contexto.getSystemService(ConnectivityManager::class.java)
    }

    var conectado by remember(gestor) {
        mutableStateOf(
            gestor?.let { tieneInternet(it) } == true
        )
    }

    // ---------- observar cambios de conexión ----------
    DisposableEffect(gestor) {
        if (gestor != null) {
            val observador = object : ConnectivityManager.NetworkCallback() {

                override fun onCapabilitiesChanged(
                    network: Network,
                    capacidades: NetworkCapabilities
                ) {
                    conectado = capacidades.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                }

                override fun onLost(network: Network) {
                    conectado = tieneInternet(gestor)
                }
            }

            gestor.registerDefaultNetworkCallback(observador)

            onDispose {
                gestor.unregisterNetworkCallback(observador)
            }
        } else {
            onDispose { }
        }
    }

    // ---------- mostrar aviso ----------
    if (!conectado) {
        AvisoSinConexion(ultimaActualizacion)
    }
}

// ---------- helpers ----------

// --- comprobar internet ---
private fun tieneInternet(gestor: ConnectivityManager): Boolean {
    val red = gestor.activeNetwork ?: return false

    val capacidades = gestor.getNetworkCapabilities(red)
        ?: return false

    return capacidades.hasCapability(
        NetworkCapabilities.NET_CAPABILITY_VALIDATED
    )
}
