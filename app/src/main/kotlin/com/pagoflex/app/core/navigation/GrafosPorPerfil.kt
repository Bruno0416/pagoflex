package com.pagoflex.app.core.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.features.cobranza.presentation.screens.CompromisosEmpresaScreen
import com.pagoflex.app.features.cobranza.presentation.screens.DetalleCompromisoEmpresaScreen
import com.pagoflex.app.features.cobranza.presentation.screens.RegistrarCompromisoScreen
import com.pagoflex.app.features.cobranza.presentation.screens.ResumenCobranzaScreen
import com.pagoflex.app.features.home.presentation.screens.HomeScreen

// Usuarios finales
fun NavGraphBuilder.grafoUsuario(
    db: LocalDatabase,
    usuarioId: Int,
    navController: NavHostController,
    onCerrarSesion: () -> Unit
) {
    composable(Ruta.Home.ruta) {
        HomeScreen(db = db, usuarioId = usuarioId)
    }
    composable(Ruta.Historial.ruta) {
        Text(stringResource(R.string.nav_historial_pendiente))
    }
    composable(Ruta.Ayuda.ruta) {
        Text(stringResource(R.string.nav_ayuda))
    }
    composable(Ruta.Avisos.ruta) {
        Text(stringResource(R.string.nav_avisos))
    }
    composable(
        route = Ruta.DetalleCompromiso.ruta,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { entrada ->
        val id = entrada.arguments?.getString("id").orEmpty()
        Text(stringResource(R.string.nav_detalle_compromiso, id))
    }
}

// Agentes
fun NavGraphBuilder.grafoAgente(
    db: LocalDatabase,
    usuarioId: Int,
    navController: NavHostController,
    onCerrarSesion: () -> Unit
) {
    composable(Ruta.BandejaReportes.ruta) {
        Text(stringResource(R.string.nav_bandeja_reportes_pendiente))
    }
}

// Ejecutivos
fun NavGraphBuilder.grafoEjecutivo(
    db: LocalDatabase,
    usuarioId: Int,
    navController: NavHostController,
    onCerrarSesion: () -> Unit
) {
    // resumen de la cobranza del mes (HU-12)
    composable(Ruta.ResumenCobranza.ruta) {
        ResumenCobranzaScreen(
            db = db,
            usuarioId = usuarioId,
            onVerCompromisos = { navController.navigate(Ruta.CompromisosEmpresa.ruta) },
            onCerrarSesion = onCerrarSesion
        )
    }
    composable(Ruta.CompromisosEmpresa.ruta) {
        CompromisosEmpresaScreen(
            db = db,
            usuarioId = usuarioId,
            onVerCompromiso = { folio -> navController.navigate(Ruta.DetalleCompromisoEmpresa.crear(folio)) }
        )
    }
    // registrar compromiso (HU-11)
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
    // detalle y anulación (HU-11)
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
