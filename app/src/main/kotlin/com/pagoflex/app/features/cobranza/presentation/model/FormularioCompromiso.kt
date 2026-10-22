package com.pagoflex.app.features.cobranza.presentation.model

// campos del formulario de registro (HU-11)
enum class CampoCompromiso {
    RUT, NOMBRES, APELLIDOS, CORREO, TELEFONO, CONCEPTO, MONTO, VENCIMIENTO, CUOTA
}

// valores tal como los escribe el ejecutivo
data class FormularioCompromiso(
    val rut: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val telefono: String = "",
    val concepto: String = "",
    val monto: String = "",
    val vencimiento: String = "",
    val numeroCuota: String = "",
    val totalCuotas: String = "",
)
