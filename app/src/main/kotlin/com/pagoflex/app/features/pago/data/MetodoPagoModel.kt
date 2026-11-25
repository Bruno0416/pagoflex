package com.pagoflex.app.features.pago.data

enum class MetodoPagoModel(val nombreCatalogo: String) {
    DEBITO("Tarjeta de débito"),
    CREDITO("Tarjeta de crédito"),
    TRANSFERENCIA("Transferencia")
}
