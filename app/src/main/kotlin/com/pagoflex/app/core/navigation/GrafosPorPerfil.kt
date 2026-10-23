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
import com.pagoflex.app.features.reportes.presentation.screens.BandejaReportesScreen
import com.pagoflex.app.features.reportes.presentation.screens.DetalleReporteScreen
import com.pagoflex.app.features.reportes.presentation.screens.MisReportesScreen
import com.pagoflex.app.features.reportes.presentation.screens.ReportarProblemaScreen

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
    // ayuda: reportes de problema del usuario (HU-06)
    composable(Ruta.Ayuda.ruta) {
        MisReportesScreen(
            db = db,
            usuarioId = usuarioId,
            onReportar = { folio -> navController.navigate(Ruta.ReportarProblema.crear(folio)) },
            onCerrarSesion = onCerrarSesion
        )
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
    composable(
        route = Ruta.ReportarProblema.ruta,
        arguments = listOf(navArgument("folio") { type = NavType.StringType })
    ) { entrada ->
        ReportarProblemaScreen(
            db = db,
            usuarioId = usuarioId,
            folio = entrada.arguments?.getString("folio").orEmpty(),
            onVolver = { navController.popBackStack() }
        )
    }
}

// Agentes
fun NavGraphBuilder.grafoAgente(
    db: LocalDatabase,
    usuarioId: Int,
    navController: NavHostController,
    onCerrarSesion: () -> Unit
) {
    // bandeja de reportes (HU-10)
    composable(Ruta.BandejaReportes.ruta) {
        BandejaReportesScreen(
            db = db,
            usuarioId = usuarioId,
            onVerReporte = { numero -> navController.navigate(Ruta.DetalleReporte.crear(numero)) },
            onCerrarSesion = onCerrarSesion
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
