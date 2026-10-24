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
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.profiles.model.PerfilUsuario

@Composable
fun AppNavegacion(
    perfilUsuario: PerfilUsuario,
    usuarioId: Int,
    db: LocalDatabase,
    // adonys: cerrar sesión desde las pantallas internas
    onCerrarSesion: () -> Unit = {}
) {
    // recrear el grafo al cambiar de usuario
    key(perfilUsuario, usuarioId) {
        val navController = rememberNavController()
        val entradaActual by navController.currentBackStackEntryAsState()
        val rutaActual = entradaActual?.destination?.route

        val rutaInicial = when (perfilUsuario) {
            PerfilUsuario.USUARIO_FINAL -> Ruta.Home.ruta
            PerfilUsuario.AGENTE -> Ruta.BandejaReportes.ruta
            PerfilUsuario.EJECUTIVO -> Ruta.ResumenCobranza.ruta
        }

        val rutasPrincipales: List<Ruta> = when (perfilUsuario) {
            PerfilUsuario.USUARIO_FINAL -> listOf(
                Ruta.Home, Ruta.Historial, Ruta.Ayuda, Ruta.Avisos
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
                if (rutasPrincipales.any { it.ruta == rutaActual }) {
                    NavigationBar {
                        rutasPrincipales.forEach { destino ->
                            NavigationBarItem(
                                selected = rutaActual == destino.ruta,
                                onClick = {
                                    navController.navigate(destino.ruta) {
                                        popUpTo(rutaInicial) { saveState = true }
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
                                                Ruta.Ayuda -> R.drawable.ic_help
                                                else -> R.drawable.ic_notifications
                                            }
                                        ),
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(
                                        when (destino) {
                                            Ruta.Home -> stringResource(R.string.nav_home)
                                            Ruta.Historial -> stringResource(R.string.nav_historial)
                                            Ruta.Ayuda -> stringResource(R.string.nav_ayuda)
                                            Ruta.Avisos -> stringResource(R.string.nav_avisos)
                                            Ruta.ResumenCobranza -> stringResource(R.string.nav_resumen)
                                            Ruta.CompromisosEmpresa -> stringResource(R.string.nav_compromisos_empresa)
                                            Ruta.RegistrarCompromiso -> stringResource(R.string.nav_registrar_compromiso)
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
                startDestination = rutaInicial,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                // Un perfil solo registra sus propias rutas.
                when (perfilUsuario) {
                    // adonys: los grafos reciben la base, el navController y cerrar sesión
                    PerfilUsuario.USUARIO_FINAL -> grafoUsuario(db, usuarioId, navController, onCerrarSesion)
                    PerfilUsuario.AGENTE -> grafoAgente(db, usuarioId, navController, onCerrarSesion)
                    PerfilUsuario.EJECUTIVO -> grafoEjecutivo(db, usuarioId, navController, onCerrarSesion)
                }
            }
        }
    }
}
