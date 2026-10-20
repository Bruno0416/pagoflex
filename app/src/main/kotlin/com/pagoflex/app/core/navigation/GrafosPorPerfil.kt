package com.pagoflex.app.core.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pagoflex.app.R
import com.pagoflex.app.core.data.local.LocalDatabase
import com.pagoflex.app.features.home.presentation.screens.HomeScreen

// Usuarios finales
fun NavGraphBuilder.grafoUsuario(db: LocalDatabase, usuarioId: Int) {
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
