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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pagoflex.app.R
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.features.home.ui.screens.HomeScreen

@Composable
fun AppNavegacion(
    perfilUsuario: PerfilUsuario = PerfilUsuario.USUARIO_FINAL
) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    val rutaInicio = when (perfilUsuario) {
        PerfilUsuario.USUARIO_FINAL -> Ruta.Home.ruta
        PerfilUsuario.AGENTE -> Ruta.BandejaReportes.ruta
        PerfilUsuario.EJECUTIVO -> Ruta.ResumenCobranza.ruta
    }

    val rutasPrincipales: List<Ruta> = when (perfilUsuario) {
        PerfilUsuario.USUARIO_FINAL -> listOf(
            Ruta.Home,
            Ruta.Historial,
            Ruta.Ayuda,
            Ruta.Avisos
        )

        PerfilUsuario.AGENTE -> emptyList()

        PerfilUsuario.EJECUTIVO -> listOf(
            Ruta.ResumenCobranza,
            Ruta.CompromisosEmpresa,
            Ruta.RegistrarCompromiso
        )
    }

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
                                    popUpTo(rutaInicio) {
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
                                            Ruta.Home, Ruta.ResumenCobranza -> R.drawable.ic_home
                                            Ruta.Historial, Ruta.CompromisosEmpresa -> R.drawable.ic_history
                                            Ruta.Ayuda, Ruta.RegistrarCompromiso -> R.drawable.ic_help
                                            else -> R.drawable.ic_notifications
                                        }
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
                                        Ruta.Avisos -> stringResource(R.string.nav_avisos)

                                        Ruta.ResumenCobranza -> "Resumen"
                                        Ruta.CompromisosEmpresa -> "Compromisos"
                                        Ruta.RegistrarCompromiso -> "Registrar"

                                        else -> ""
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
            startDestination = rutaInicio,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            when (perfilUsuario) {
                PerfilUsuario.USUARIO_FINAL -> {
                    composable(Ruta.Home.ruta) {
                        HomeScreen { }
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

                PerfilUsuario.AGENTE -> {
                    composable(Ruta.BandejaReportes.ruta) {
                        Text("Bandeja de reportes pendiente")
                    }
                }

                PerfilUsuario.EJECUTIVO -> {
                    composable(Ruta.ResumenCobranza.ruta) {
                        Text("Resumen de cobranza pendiente")
                    }

                    composable(Ruta.CompromisosEmpresa.ruta) {
                        Text("Compromisos de empresa pendiente")
                    }

                    composable(Ruta.RegistrarCompromiso.ruta) {
                        Text("Registrar compromiso pendiente")
                    }
                }
            }
        }
    }
}