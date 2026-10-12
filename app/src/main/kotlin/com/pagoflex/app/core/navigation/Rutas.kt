package com.pagoflex.app.core.navigation

sealed class Ruta(val ruta: String) {
    data object Home :  Ruta("home")

    data object Historial : Ruta("historial")

    data object Ayuda : Ruta("ayuda")

    data object Avisos : Ruta("avisos")

    data object DetalleCompromiso : Ruta("compromiso/{id}"){
        fun crear(id: String) = "compromiso/$id"
    }

}