package com.pagoflex.app.core.navigation

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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pagoflex.app.R

@Composable
fun AppNavegacion() {

    val navController = rememberNavController()

    val entradaActual by
    navController.currentBackStackEntryAsState()

    val rutaActual = entradaActual?.destination?.route

    val rutasPrincipales = listOf(
        Ruta.Home,
        Ruta.Historial,
        Ruta.Ayuda,
        Ruta.Avisos
    )

    Scaffold(
        bottomBar = {
            if (rutasPrincipales.any {
                    it.ruta == rutaActual
                }) {
                NavigationBar {
                    rutasPrincipales.forEach { destino ->

                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = {
                                navController.navigate(destino.ruta) {
                                    popUpTo(Ruta.Home.ruta) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        when (destino) {
                                            Ruta.Home -> R.drawable.ic_home
                                            Ruta.Historial -> R.drawable.ic_history
                                            Ruta.Ayuda -> R.drawable.ic_help
                                            else -> R.drawable.ic_notifications
                                        },
                                    ),
                                    contentDescription = null, // TODO: cambiar

                                )
                            },
                            label = {
                                Text(
                                    when (destino) {
                                        Ruta.Home -> stringResource(R.string.nav_home)
                                        Ruta.Historial -> stringResource(R.string.nav_historial)
                                        Ruta.Ayuda -> stringResource(R.string.nav_ayuda)
                                        else -> stringResource(R.string.nav_avisos)
                                    }
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
            modifier = Modifier.padding(padding)
        ) {

            composable(Ruta.Home.ruta) {
                Text("Inicio")
            }

            composable(Ruta.Historial.ruta) {
                Text("Historial pendiente")
            }

            composable(Ruta.Ayuda.ruta) {
                Text("Ayuda")
            }
            composable(Ruta.Avisos.ruta) {
                Text("Avisos")
            }

            composable(
                route = Ruta.DetalleCompromiso.ruta,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                    }
                )
            ) { entrada ->

                val id = entrada.arguments
                    ?.getString("id")
                    .orEmpty()

                Text("Detalle del compromiso $id")
            }
        }
    }
}