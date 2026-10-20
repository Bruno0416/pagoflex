package com.pagoflex.app.features.auth.presentation.validators

import androidx.annotation.StringRes
import com.pagoflex.app.R

object AuthValidators {
    private val correoRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    @StringRes
    fun validarCorreo(correo: String): Int? {
        return when {
            correo.trim().isBlank() -> R.string.auth_error_correo_obligatorio

            !correoRegex.matches(correo.trim()) -> R.string.auth_error_correo_invalido

            else -> null
        }
    }

    @StringRes
    fun validarContrasena(contrasena: String): Int? {
        return when {
            contrasena.isBlank() -> R.string.auth_error_contrasena_obligatoria

            contrasena.length < 6 -> R.string.auth_error_contrasena_minima

            else -> null
        }
    }

    @StringRes
    fun validarConfirmarContrasena(contrasena: String, confirmarContrasena: String): Int? {
        return when {
            confirmarContrasena.isBlank() -> R.string.auth_error_confirmacion_obligatoria

            contrasena != confirmarContrasena -> R.string.auth_error_contrasenas_distintas

            else -> null
        }
    }

    fun validarNombres(nombres: String): Int? {
        return if (nombres.isBlank()) {
            R.string.auth_error_nombres_obligatorios
        } else {
            null
        }
    }

    fun validarApellidos(apellidos: String): Int? {
        return if (apellidos.isBlank()) {
            R.string.auth_error_apellidos_obligatorios
        } else {
            null
        }
    }

    fun normalizarNombre(texto: String): String {
        return texto.trim()
            .lowercase()
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { it.replaceFirstChar { letra -> letra.titlecase() } }
    }

    @StringRes
    fun validarRut(rut: String): Int? {
        if (rut.isBlank()) return R.string.auth_error_rut_obligatorio

        // 1. limpiar formato
        val rutLimpio = rut.replace(".", "")
            .replace("-", "")
            .trim()
            .uppercase()

        // 2. validar estructura
        val formatoRut = Regex("""^\d{1,8}[0-9K]$""")

        if (!formatoRut.matches(rutLimpio)) return R.string.auth_error_rut_invalido

        // 3. calcular dv
        val cuerpo = rutLimpio.dropLast(1)
        val dv = rutLimpio.last()

        var suma = 0
        var multiplicador = 2

        for (d in cuerpo.reversed()) {
            suma += d.digitToInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val dvEsperado = when (val resultado = 11 - (suma % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> resultado.digitToChar()
        }

        // 4. comparar DVs
        return if (dv == dvEsperado) null else R.string.auth_error_rut_invalido
    }

    fun normalizarRut(rut: String): String {
        val limpio = rut.trim()
            .replace(".", "")
            .replace("-", "")
            .uppercase()

        val cuerpo = limpio.dropLast(1)
        val digitoVerificador = limpio.last()

        // agregar puntos cada tres dígitos desde la derecha
        val cuerpoFormateado = cuerpo.reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()

        return "$cuerpoFormateado-$digitoVerificador"
    }
}