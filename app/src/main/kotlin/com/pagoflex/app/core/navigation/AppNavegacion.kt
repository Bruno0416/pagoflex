package com.pagoflex.app.core.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.features.perfil.presentation.screens.PerfilScreen

@Composable
fun AppNavegacion(
    perfilUsuario: PerfilUsuario,
    usuarioId: Int,
    db: LocalDatabase,
    onCerrarSesion: () -> Unit
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
                Ruta.Home, Ruta.Historial, Ruta.Ayuda

            //Ruta.Avisos (RF-11 | HU-07 queda pendiente para la siguiente entrega,
            // el mvp ya cumple RF-10 mediante reconocimientos (RF-13))
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
                                                Ruta.Home -> R.drawable.ic_home
                                                Ruta.Historial -> R.drawable.ic_history
                                                Ruta.Ayuda -> R.drawable.ic_help
                                                // adonys: íconos propios del ejecutivo
                                                Ruta.ResumenCobranza -> R.drawable.ic_bar_chart
                                                Ruta.CompromisosEmpresa -> R.drawable.ic_list
                                                Ruta.RegistrarCompromiso -> R.drawable.ic_add
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
                                            // Ruta.Avisos -> stringResource(R.string.nav_avisos)
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
                    .consumeWindowInsets(padding),
                // transiciones breves para mantener el contexto al navegar
                enterTransition = {
                    fadeIn(animationSpec = tween(180)) +
                        slideInHorizontally(
                            initialOffsetX = { it / 16 },
                            animationSpec = tween(180, easing = FastOutSlowInEasing)
                        )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(130))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(160)) +
                        slideInHorizontally(
                            initialOffsetX = { -it / 16 },
                            animationSpec = tween(160, easing = FastOutSlowInEasing)
                        )
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(130)) +
                        slideOutHorizontally(
                            targetOffsetX = { it / 16 },
                            animationSpec = tween(160, easing = FastOutSlowInEasing)
                        )
                }
            ) {
                // Preparada como ruta común; se enlaza solo desde Home en esta entrega.
                composable(Ruta.Perfil.ruta) {
                    PerfilScreen(
                        db = db,
                        usuarioId = usuarioId,
                        onVolver = { navController.popBackStack() },
                        onCerrarSesion = onCerrarSesion
                    )
                }
                // Un perfil solo registra sus propias rutas.
                when (perfilUsuario) {
                    // adonys: los grafos reciben la base y el navController
                    PerfilUsuario.USUARIO_FINAL -> grafoUsuario(db, usuarioId, navController)
                    PerfilUsuario.AGENTE -> grafoAgente(db, usuarioId, navController)
                    PerfilUsuario.EJECUTIVO -> grafoEjecutivo(db, usuarioId, navController)
                }
            }
        }
    }
}
