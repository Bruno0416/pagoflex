package com.pagoflex.app.core.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pagoflex.app.R
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun AppNavegacion(onCerrarSesion: () -> Unit) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val pestanas = listOf(Ruta.Home, Ruta.Historial, Ruta.Ayuda)

    Scaffold(
        containerColor = Theme.colores.background,
        bottomBar = {
            if (pestanas.any { it.ruta == rutaActual }) {
                NavigationBar(
                    containerColor = Theme.colores.surfaceContainer,
                    contentColor = Theme.colores.onSurface
                ) {
                    pestanas.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = {
                                if (navController.currentDestination?.route != destino.ruta) {
                                    navController.navigate(destino.ruta) {
                                        popUpTo(Ruta.Home.ruta) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        when (destino) {
                                            Ruta.Home -> R.drawable.ic_home
                                            Ruta.Historial -> R.drawable.ic_history
                                            else -> R.drawable.ic_help
                                        }
                                    ),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    stringResource(
                                        when (destino) {
                                            Ruta.Home -> R.string.nav_home
                                            Ruta.Historial -> R.string.nav_historial
                                            else -> R.string.nav_ayuda
                                        }
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Ruta.Home.ruta,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding)
        ) {
            grafoUsuario(navController, onCerrarSesion)
        }
    }
}
