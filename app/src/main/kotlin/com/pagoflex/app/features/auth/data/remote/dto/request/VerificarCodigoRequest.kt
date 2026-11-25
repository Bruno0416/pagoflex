package com.pagoflex.app.features.auth.data.remote.dto.request

data class VerificarCodigoRequest(
    val correo: String,
    val codigo: String
)
