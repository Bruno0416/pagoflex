package com.pagoflex.app.core.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.features.cobranza.presentation.screens.CompromisosEmpresaScreen
import com.pagoflex.app.features.cobranza.presentation.screens.DetalleCompromisoEmpresaScreen
import com.pagoflex.app.features.cobranza.presentation.screens.RegistrarCompromisoScreen
import com.pagoflex.app.features.cobranza.presentation.screens.ResumenCobranzaScreen
import com.pagoflex.app.features.cumplimiento.presentation.screens.CumplimientoScreen
import com.pagoflex.app.features.detalle.presentation.screens.DetalleCompromisoScreen
import com.pagoflex.app.features.historial.presentation.screens.ComprobanteScreen
import com.pagoflex.app.features.historial.presentation.screens.HistorialScreen
import com.pagoflex.app.features.home.presentation.screens.HomeScreen
import com.pagoflex.app.features.home.presentation.screens.ListaCompromisosScreen
import com.pagoflex.app.features.perfil.presentation.screens.CambiarContrasenaScreen
import com.pagoflex.app.features.perfil.presentation.screens.PerfilScreen
import com.pagoflex.app.features.reconocimientos.presentation.screens.ReconocimientosScreen
import com.pagoflex.app.features.reportes.presentation.screens.BandejaReportesScreen
import com.pagoflex.app.features.reportes.presentation.screens.DetalleReporteScreen
import com.pagoflex.app.features.reportes.presentation.screens.MisReportesScreen
import com.pagoflex.app.features.mensajeria.presentation.screens.BandejaMensajesScreen
import com.pagoflex.app.features.mensajeria.presentation.model.RolChat
import com.pagoflex.app.features.reportes.presentation.screens.ReportarProblemaScreen

// ---------- perfil (agente y ejecutivo) ----------
fun NavGraphBuilder.grafoPerfil(
    navController: NavHostController,
    onCerrarSesion: () -> Unit
) {
    composable(Ruta.Perfil.ruta) {
        PerfilScreen(
            onVolver = { navController.volverDesde(Ruta.Perfil.ruta) },
            onCerrarSesion = onCerrarSesion,
            onCambiarContrasena = {
                if (navController.currentDestination?.route == Ruta.Perfil.ruta) {
                    navController.navigate(Ruta.CambiarContrasena.ruta) { launchSingleTop = true }
                }
            }
        )
    }
    composable(Ruta.CambiarContrasena.ruta) { entrada ->
        val perfilOwner = remember(entrada) { navController.getBackStackEntry(Ruta.Perfil.ruta) }
        CambiarContrasenaScreen(
            perfilOwner = perfilOwner,
            onVolver = { navController.volverDesde(Ruta.CambiarContrasena.ruta) }
        )
    }
}

// ---------- usuario final ----------
fun NavGraphBuilder.grafoUsuario(
    db: LocalDatabase,
    navController: NavHostController,
    onCerrarSesion: () -> Unit
) {
    // ---------- inicio y compromisos ----------
    composable(Ruta.Home.ruta) {
        HomeScreen(
            onVerTodos = { navController.navigate(Ruta.ListaCompromisos.ruta) },
            onVerCompromiso = { folio ->
                navController.navigate(Ruta.DetalleCompromiso.ver(folio))
            },
            onVerPerfil = {
                // 1. abrir perfil solo si inicio sigue siendo la ruta activa
                if (navController.currentDestination?.route == Ruta.Home.ruta) {
                    navController.navigate(Ruta.Perfil.ruta) {
                        launchSingleTop = true
                    }
                }
            },
            onVerCumplimiento = { navController.navigate(Ruta.Cumplimiento.ruta) }
        )
    }
    composable(Ruta.ListaCompromisos.ruta) {
        ListaCompromisosScreen(
            onVolver = { navController.volverDesde(Ruta.ListaCompromisos.ruta) },
            onVerCompromiso = { folio ->
                navController.navigate(Ruta.DetalleCompromiso.ver(folio))
            }
        )
    }
    composable(
        route = Ruta.DetalleCompromiso.ruta,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { entrada ->
        val folio = entrada.arguments?.getString("id").orEmpty()
        DetalleCompromisoScreen(
            folio = folio,
            onVolver = { navController.volverDesde(Ruta.DetalleCompromiso.ruta) },
            onReportar = { navController.navigate(Ruta.ReportarProblema.crear(folio)) }
        )
    }
    composable(
        route = Ruta.ReportarProblema.ruta,
        arguments = listOf(navArgument("folio") { type = NavType.StringType })
    ) { entrada ->
        ReportarProblemaScreen(
            db = db,
            usuarioId = 0,
            folio = entrada.arguments?.getString("folio").orEmpty(),
            onVolver = { navController.popBackStack() }
        )
    }

    // ---------- historial y comprobantes ----------
    composable(Ruta.Historial.ruta) {
        HistorialScreen(
            onVerComprobante = { numero ->
                navController.navigate(Ruta.DetalleComprobante.ver(numero))
            }
        )
    }
    composable(
        route = Ruta.DetalleComprobante.ruta,
        arguments = listOf(navArgument("numero") { type = NavType.StringType })
    ) { entrada ->
        ComprobanteScreen(
            numeroComprobante = entrada.arguments?.getString("numero").orEmpty(),
            onCerrar = { navController.volverDesde(Ruta.DetalleComprobante.ruta) }
        )
    }

    // ---------- perfil y cumplimiento ----------
    composable(Ruta.Perfil.ruta) {
        PerfilScreen(
            onVolver = { navController.volverDesde(Ruta.Perfil.ruta) },
            onCerrarSesion = onCerrarSesion,
            onCambiarContrasena = {
                if (navController.currentDestination?.route == Ruta.Perfil.ruta) {
                    navController.navigate(Ruta.CambiarContrasena.ruta) { launchSingleTop = true }
                }
            }
        )
    }
    composable(Ruta.CambiarContrasena.ruta) { entrada ->
        val perfilOwner = remember(entrada) { navController.getBackStackEntry(Ruta.Perfil.ruta) }
        CambiarContrasenaScreen(
            perfilOwner = perfilOwner,
            onVolver = { navController.volverDesde(Ruta.CambiarContrasena.ruta) }
        )
    }
    composable(Ruta.Cumplimiento.ruta) {
        CumplimientoScreen(
            onVolver = { navController.volverDesde(Ruta.Cumplimiento.ruta) },
            onVerReconocimientos = {
                if (navController.currentDestination?.route == Ruta.Cumplimiento.ruta) {
                    navController.navigate(Ruta.Reconocimientos.ruta) { launchSingleTop = true }
                }
            }
        )
    }
    composable(Ruta.Reconocimientos.ruta) {
        ReconocimientosScreen(
            onVolver = { navController.volverDesde(Ruta.Reconocimientos.ruta) }
        )
    }

    // ---------- ayuda ----------
    composable(Ruta.Ayuda.ruta) {
        MisReportesScreen(
            db = db,
            usuarioId = 0,
            onReportar = { folio -> navController.navigate(Ruta.ReportarProblema.crear(folio)) }
        )
    }
}

// Agentes
fun NavGraphBuilder.grafoAgente(
    db: LocalDatabase,
    usuarioId: Int,
    navController: NavHostController
) {
    composable(Ruta.Mensajes.ruta) { BandejaMensajesScreen(RolChat.AGENTE) }

    // adonys: bandeja de reportes (HU-10)
    composable(Ruta.BandejaReportes.ruta) {
        BandejaReportesScreen(
            db = db,
            usuarioId = usuarioId,
            onVerReporte = { numero -> navController.navigate(Ruta.DetalleReporte.crear(numero)) },
            onVerPerfil = { navController.navigate(Ruta.Perfil.ruta) }
        )
    }
    composable(
        route = Ruta.DetalleReporte.ruta,
        arguments = listOf(navArgument("numero") { type = NavType.StringType })
    ) { entrada ->
        DetalleReporteScreen(
            db = db,
            usuarioId = usuarioId,
            numero = entrada.arguments?.getString("numero").orEmpty(),
            onVolver = { navController.popBackStack() }
        )
    }
}

// Ejecutivos
fun NavGraphBuilder.grafoEjecutivo(
    db: LocalDatabase,
    usuarioId: Int,
    navController: NavHostController
) {
    composable(Ruta.Mensajes.ruta) { BandejaMensajesScreen(RolChat.EJECUTIVO) }

    // adonys: resumen de la cobranza del mes (HU-12)
    composable(Ruta.ResumenCobranza.ruta) {
        ResumenCobranzaScreen(
            db = db,
            usuarioId = usuarioId,
            // adonys: ir a la pestaña Compromisos igual que la barra inferior
            onVerCompromisos = { navController.irAPestana(Ruta.CompromisosEmpresa.ruta, Ruta.ResumenCobranza.ruta) },
            onVerPerfil = { navController.navigate(Ruta.Perfil.ruta) }
        )
    }
    composable(Ruta.CompromisosEmpresa.ruta) {
        CompromisosEmpresaScreen(
            db = db,
            usuarioId = usuarioId,
            onVerCompromiso = { folio -> navController.navigate(Ruta.DetalleCompromisoEmpresa.crear(folio)) }
        )
    }
    // adonys: registrar compromiso (HU-11)
    composable(Ruta.RegistrarCompromiso.ruta) {
        RegistrarCompromisoScreen(
            db = db,
            usuarioId = usuarioId,
            onVolverResumen = {
                navController.navigate(Ruta.ResumenCobranza.ruta) {
                    popUpTo(Ruta.ResumenCobranza.ruta) { inclusive = true }
                }
            }
        )
    }
    // adonys: detalle y anulación (HU-11)
    composable(
        route = Ruta.DetalleCompromisoEmpresa.ruta,
        arguments = listOf(navArgument("folio") { type = NavType.StringType })
    ) { entrada ->
        DetalleCompromisoEmpresaScreen(
            db = db,
            usuarioId = usuarioId,
            folio = entrada.arguments?.getString("folio").orEmpty(),
            onVolver = { navController.popBackStack() }
        )
    }
}

// adonys: navegar como la barra inferior para no romper el volver de las pestañas
private fun NavHostController.irAPestana(ruta: String, rutaInicial: String) {
    navigate(ruta) {
        popUpTo(rutaInicial) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

internal fun NavHostController.volverDesde(rutaOrigen: String) {
    if (currentDestination?.route != rutaOrigen || previousBackStackEntry == null) return
    popBackStack()
}
