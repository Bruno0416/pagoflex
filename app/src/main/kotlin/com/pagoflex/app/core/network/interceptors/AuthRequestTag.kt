package com.pagoflex.app.core.network.interceptors

internal data class AuthRequestTag(
    val publico: Boolean,
    val sesionKey: String? = null
)
