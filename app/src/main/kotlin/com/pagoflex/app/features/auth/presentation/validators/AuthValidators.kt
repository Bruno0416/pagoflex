package com.pagoflex.app.features.auth.presentation.validators

import androidx.annotation.StringRes
import com.pagoflex.app.R

data class RequisitosContrasenaEstado(
    val longitud: Boolean,
    val mayuscula: Boolean,
    val minuscula: Boolean,
    val numero: Boolean,
    val simbolo: Boolean
)

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

    private val mayusculaRegex = Regex("""\p{Lu}""")
    private val minusculaRegex = Regex("""\p{Ll}""")
    private val numeroRegex = Regex("""\p{Nd}""")
    private val simboloRegex = Regex("""[^\p{L}\p{N}\s]""")
    private val contrasenaSeguraRegex =
        Regex("""^(?=.*\p{Lu})(?=.*\p{Ll})(?=.*\p{Nd})(?=.*[^\p{L}\p{N}\s]).{8,}$""")

    fun requisitosContrasena(contrasena: String): RequisitosContrasenaEstado = RequisitosContrasenaEstado(
        longitud = contrasena.length >= 8,
        mayuscula = mayusculaRegex.containsMatchIn(contrasena),
        minuscula = minusculaRegex.containsMatchIn(contrasena),
        numero = numeroRegex.containsMatchIn(contrasena),
        simbolo = simboloRegex.containsMatchIn(contrasena)
    )

    @StringRes
    fun validarContrasenaSegura(contrasena: String): Int? {
        val requisitos = requisitosContrasena(contrasena)
        return when {
            contrasena.isBlank() -> R.string.auth_error_contrasena_obligatoria
            !requisitos.longitud -> R.string.auth_error_contrasena_segura_longitud
            !requisitos.mayuscula -> R.string.auth_error_contrasena_segura_mayuscula
            !requisitos.minuscula -> R.string.auth_error_contrasena_segura_minuscula
            !requisitos.numero -> R.string.auth_error_contrasena_segura_numero
            !requisitos.simbolo -> R.string.auth_error_contrasena_segura_especial
            !contrasenaSeguraRegex.matches(contrasena) -> R.string.auth_error_contrasena_segura
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

    private val nombreRegex = Regex("""^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$""")

    fun validarNombres(nombres: String): Int? = when {
        nombres.isBlank() -> R.string.auth_error_nombres_obligatorios
        !nombreRegex.matches(nombres) -> R.string.auth_api_nombres_invalidos
        else -> null
    }

    fun validarApellidos(apellidos: String): Int? = when {
        apellidos.isBlank() -> R.string.auth_error_apellidos_obligatorios
        !nombreRegex.matches(apellidos) -> R.string.auth_api_apellidos_invalidos
        else -> null
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

        val rutLimpio = rut.replace(".", "")
            .replace("-", "")
            .trim()
            .uppercase()

        val formatoRut = Regex("""^\d{7,8}[0-9K]$""")

        if (!formatoRut.matches(rutLimpio)) return R.string.auth_error_rut_invalido

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

        return if (dv == dvEsperado) null else R.string.auth_error_rut_invalido
    }

    fun normalizarRut(rut: String): String {
        val limpio = rut.trim()
            .replace(".", "")
            .replace("-", "")
            .uppercase()

        if (limpio.isEmpty()) return ""
        return "${limpio.dropLast(1)}-${limpio.last()}"
    }
}
