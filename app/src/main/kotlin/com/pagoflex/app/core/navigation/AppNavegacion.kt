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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.network.auth.model.PerfilUsuario
import com.pagoflex.app.core.ui.theme.Theme

@Composable
fun AppNavegacion(perfil: PerfilUsuario, onCerrarSesion: () -> Unit) {
    val navController = rememberNavController()
    val contexto = LocalContext.current.applicationContext
    val db = remember { LocalDatabase.obtenerInstancia(contexto) }
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val pestanas = when (perfil) {
        PerfilUsuario.USUARIO_FINAL -> listOf(Ruta.Home, Ruta.Historial, Ruta.Ayuda)
        PerfilUsuario.AGENTE -> emptyList()
        PerfilUsuario.EJECUTIVO -> listOf(Ruta.ResumenCobranza, Ruta.CompromisosEmpresa, Ruta.RegistrarCompromiso)
    }
    val rutaInicial = when (perfil) {
        PerfilUsuario.USUARIO_FINAL -> Ruta.Home.ruta
        PerfilUsuario.AGENTE -> Ruta.BandejaReportes.ruta
        PerfilUsuario.EJECUTIVO -> Ruta.ResumenCobranza.ruta
    }

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
                                        popUpTo(rutaInicial) { saveState = true }
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
                                            Ruta.Ayuda -> R.drawable.ic_help
                                            Ruta.ResumenCobranza -> R.drawable.ic_bar_chart
                                            Ruta.CompromisosEmpresa -> R.drawable.ic_list
                                            else -> R.drawable.ic_add
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
                                            Ruta.Ayuda -> R.string.nav_ayuda
                                            Ruta.ResumenCobranza -> R.string.nav_resumen
                                            Ruta.CompromisosEmpresa -> R.string.nav_compromisos_empresa
                                            else -> R.string.nav_registrar_compromiso
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
            startDestination = rutaInicial,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding)
        ) {
            when (perfil) {
                PerfilUsuario.USUARIO_FINAL -> grafoUsuario(db, navController, onCerrarSesion)
                PerfilUsuario.AGENTE -> {
                    grafoPerfil(navController, onCerrarSesion)
                    grafoAgente(db, 0, navController)
                }
                PerfilUsuario.EJECUTIVO -> {
                    grafoPerfil(navController, onCerrarSesion)
                    grafoEjecutivo(db, 0, navController)
                }
            }
        }
    }
}
