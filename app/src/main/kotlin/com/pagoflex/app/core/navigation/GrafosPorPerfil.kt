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
import com.pagoflex.app.features.detalle.presentation.screens.DetalleCompromisoScreen
import com.pagoflex.app.features.historial.presentation.screens.ComprobanteScreen
import com.pagoflex.app.features.historial.presentation.screens.HistorialScreen
import com.pagoflex.app.features.home.presentation.screens.HomeScreen
import com.pagoflex.app.features.home.presentation.screens.ListaCompromisosScreen

// Usuarios finales
fun NavGraphBuilder.grafoUsuario(
    db: LocalDatabase,
    usuarioId: Int,
    navController: NavHostController
) {
    composable(Ruta.Home.ruta) {
        HomeScreen(
            db = db,
            usuarioId = usuarioId,
            onVerTodos = { navController.navigate(Ruta.ListaCompromisos.ruta) },
            onVerCompromiso = { folio ->
                navController.navigate(Ruta.DetalleCompromiso.ver(folio))
            }
        )
    }
    composable(Ruta.ListaCompromisos.ruta) {
        ListaCompromisosScreen(
            db = db,
            usuarioId = usuarioId,
            onVolver = { navController.popBackStack() },
            onVerCompromiso = { folio ->
                navController.navigate(Ruta.DetalleCompromiso.ver(folio))
            }
        )
    }

    // ---------- historial ----------
    composable(Ruta.Historial.ruta) {
        HistorialScreen(
            db = db,
            usuarioId = usuarioId,
            onVerComprobante = { numero ->
                navController.navigate(
                    Ruta.DetalleComprobante.ver(numero)
                )
            }
        )
    }

    composable(Ruta.Ayuda.ruta) {
        Text(stringResource(R.string.nav_ayuda))
    }
    composable(Ruta.Avisos.ruta) {
        Text(stringResource(R.string.nav_avisos))
    }

    // ---------- detalle de comprobante ----------
    composable(
        route = Ruta.DetalleComprobante.ruta,
        arguments = listOf(
            navArgument("numero") {
                type = NavType.StringType
            }
        )
    ) { entrada ->
        val numero = entrada.arguments?.getString("numero").orEmpty()

        ComprobanteScreen(
            db = db,
            usuarioId = usuarioId,
            numeroComprobante = numero,
            onCerrar = {
                navController.popBackStack()
            },
        )
    }

    // ---------- detalle de compromiso ----------
    composable(
        route = Ruta.DetalleCompromiso.ruta,
        arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )
    ) { entrada ->
        val folio = entrada.arguments?.getString("id").orEmpty()

        DetalleCompromisoScreen(
            db = db,
            usuarioId = usuarioId,
            folio = folio,
            onVolver = {
                navController.popBackStack()
            }
        )
    }
}

// Agentes
fun NavGraphBuilder.grafoAgente() {
    composable(Ruta.BandejaReportes.ruta) {
        Text(stringResource(R.string.nav_bandeja_reportes_pendiente))
    }
}

// Ejecutivos
fun NavGraphBuilder.grafoEjecutivo() {
    composable(Ruta.ResumenCobranza.ruta) {
        Text(stringResource(R.string.nav_resumen_cobranza_pendiente))
    }
    composable(Ruta.CompromisosEmpresa.ruta) {
        Text(stringResource(R.string.nav_compromisos_empresa_pendiente))
    }
    composable(Ruta.RegistrarCompromiso.ruta) {
        Text(stringResource(R.string.nav_registrar_compromiso_pendiente))
    }
}
