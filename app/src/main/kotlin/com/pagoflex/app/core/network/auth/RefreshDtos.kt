package com.pagoflex.app.core.network.auth

data class RefreshRequest(
    val tokenRenovacion: String
)

data class RefreshResponse(
    val tokenAcceso: String,
    val tokenRenovacion: String?
)
