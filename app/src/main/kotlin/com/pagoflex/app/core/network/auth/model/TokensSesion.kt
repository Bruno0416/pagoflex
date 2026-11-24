package com.pagoflex.app.core.network.auth.model

data class TokensSesion(
    val accessToken: String,
    val refreshToken: String
)

data class SesionAutenticada(
    val usuario: SesionUsuario,
    val tokens: TokensSesion,
    val sesionKey: String
)
