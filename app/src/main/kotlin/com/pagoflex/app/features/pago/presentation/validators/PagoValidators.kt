package com.pagoflex.app.features.pago.presentation.validators

import androidx.annotation.StringRes
import com.pagoflex.app.R
import java.time.LocalDate
import java.time.YearMonth

object PagoValidators {
    @StringRes
    fun validarNumeroTarjeta(numero: String): Int? =
        if (numero.length == 16 && numero.all(Char::isDigit)) null
        else R.string.pago_error_numero_tarjeta

    @StringRes
    fun validarVencimientoTarjeta(vencimiento: String, hoy: LocalDate = LocalDate.now()): Int? {
        // 1. comprobar que el vencimiento tenga cuatro dígitos
        if (vencimiento.length != 4 || !vencimiento.all(Char::isDigit)) {
            return R.string.pago_error_vencimiento_tarjeta
        }

        // 2. interpretar el mes y el año de vencimiento
        val mes = vencimiento.take(2).toInt()
        if (mes !in 1..12) return R.string.pago_error_vencimiento_tarjeta
        val anio = 2000 + vencimiento.drop(2).toInt()
        val fecha = YearMonth.of(anio, mes)

        // 3. permitir el mes actual y rechazar meses anteriores
        return if (fecha.isBefore(YearMonth.from(hoy))) {
            R.string.pago_error_vencimiento_vencido
        } else {
            null
        }
    }

    @StringRes
    fun validarCodigoSeguridad(codigo: String): Int? =
        if (codigo.length in 3..4 && codigo.all(Char::isDigit)) null
        else R.string.pago_error_codigo_seguridad
}
