package com.pagoflex.app.core.navigation

sealed class Ruta(val ruta: String) {
    // Ruta compartida por los tres roles; solo usuario final tiene acceso UI en esta entrega.
    data object Perfil : Ruta("perfil")

    // Usuario final
    data object Home : Ruta("home")
    data object ListaCompromisos : Ruta("compromisos")
    data object Historial : Ruta("historial")
    data object Ayuda : Ruta("ayuda")
    data object Avisos : Ruta("avisos")
    data object Cumplimiento : Ruta("cumplimiento")
    data object Reconocimientos : Ruta("reconocimientos")

    // --- rutas secundarias ---
    data object DetalleCompromiso : Ruta("compromiso/{id}") {
        fun ver(id: String) = "compromiso/$id"
    }

    data object DetalleComprobante : Ruta("historial/comprobante/{numero}") {
        fun ver(numero: String) = "historial/comprobante/$numero"
    }


    // Agente
    data object BandejaReportes : Ruta("agente/reportes")

    // --- rutas secundarias ---

    // Ejecutivo
    data object ResumenCobranza : Ruta("ejecutivo/resumen")
    data object CompromisosEmpresa : Ruta("ejecutivo/compromisos")
    data object RegistrarCompromiso : Ruta("ejecutivo/registrar")

    // --- rutas secundarias ---

}