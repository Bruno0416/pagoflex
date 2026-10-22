package com.pagoflex.app.core.validators

import androidx.annotation.StringRes
import com.pagoflex.app.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

// validaciones compartidas de formularios internos (RN-17, RN-18, RN-20, RN-21)
// cada función devuelve el id del mensaje de error o null si el valor es válido
object Validaciones {

    // strict para rechazar fechas inexistentes como 31-02-2026
    private val formatoFecha: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd-MM-uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    private val correoRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    // ---------- rut (RN-18) ----------

    fun limpiarRut(rut: String): String =
        rut.replace(".", "")
            .replace("-", "")
            .replace(" ", "")
            .uppercase()

    // dígito verificador con módulo 11
    fun calcularDv(cuerpo: String): Char {
        var suma = 0
        var multiplicador = 2

        for (digito in cuerpo.reversed()) {
            suma += digito.digitToInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        return when (val resultado = 11 - (suma % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> resultado.digitToChar()
        }
    }

    @StringRes
    fun validarRut(rut: String): Int? {
        // 1. limpiar formato
        val limpio = limpiarRut(rut)
        if (limpio.isEmpty()) return R.string.val_rut_obligatorio

        // 2. validar estructura
        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last()
        val estructuraValida = cuerpo.length in 6..8 &&
                cuerpo.all { it.isDigit() } &&
                (dv.isDigit() || dv == 'K')
        if (!estructuraValida) return R.string.val_rut_formato

        // 3. comparar dígito verificador
        return if (calcularDv(cuerpo) == dv) null else R.string.val_rut_dv
    }

    // mismo formato que guarda el registro: 15.834.207-3
    fun formatearRut(rut: String): String {
        val limpio = limpiarRut(rut)
        if (limpio.length < 2) return rut

        val cuerpo = limpio.dropLast(1)
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()

        return "$cuerpo-${limpio.last()}"
    }

    // ---------- montos (RN-17) ----------

    // acepta "310000", "310.000" o "$ 310.000"
    fun parsearMonto(texto: String): Long? {
        val limpio = texto.replace("$", "").replace(".", "").replace(" ", "")
        if (limpio.isEmpty() || !limpio.all { it.isDigit() }) return null
        return limpio.toLongOrNull()
    }

    @StringRes
    fun validarMonto(texto: String): Int? {
        if (texto.isBlank()) return R.string.val_monto_obligatorio

        val monto = parsearMonto(texto) ?: return R.string.val_monto_entero

        return if (monto > 0) null else R.string.val_monto_mayor_cero
    }

    // ---------- fechas (RN-17, RN-20) ----------

    fun parsearFecha(texto: String): LocalDate? =
        try {
            LocalDate.parse(texto.trim(), formatoFecha)
        } catch (_: DateTimeParseException) {
            null
        }

    // el vencimiento no puede ser anterior a hoy
    @StringRes
    fun validarFechaVencimiento(texto: String, hoy: LocalDate): Int? {
        if (texto.isBlank()) return R.string.val_fecha_obligatoria

        val fecha = parsearFecha(texto) ?: return R.string.val_fecha_formato

        return if (fecha.isBefore(hoy)) R.string.val_fecha_pasada else null
    }

    // ---------- cuotas (RN-20) ----------

    // la cuota es opcional, pero si viene una tienen que venir las dos
    @StringRes
    fun validarCuota(numero: String, total: String): Int? {
        if (numero.isBlank() && total.isBlank()) return null

        val n = numero.trim().toIntOrNull()
        val t = total.trim().toIntOrNull()

        return when {
            n == null || t == null -> R.string.val_cuota_incompleta
            n <= 0 || t <= 0 -> R.string.val_cuota_mayor_cero
            n > t -> R.string.val_cuota_mayor_total
            else -> null
        }
    }

    // ---------- textos ----------

    @StringRes
    fun validarObligatorio(texto: String, @StringRes error: Int): Int? =
        if (texto.isBlank()) error else null

    // con motivo "otro" el comentario es obligatorio (RN-09, RN-21)
    @StringRes
    fun validarComentario(motivoEsOtro: Boolean, comentario: String): Int? =
        if (motivoEsOtro && comentario.isBlank()) R.string.val_comentario_otro else null

    @StringRes
    fun validarCorreo(correo: String): Int? =
        when {
            correo.isBlank() -> R.string.val_correo_obligatorio
            !correoRegex.matches(correo.trim()) -> R.string.val_correo_invalido
            else -> null
        }

    // solo se cuentan los dígitos: +56 9 0000 0101 tiene 11
    @StringRes
    fun validarTelefono(telefono: String): Int? {
        if (telefono.isBlank()) return R.string.val_telefono_obligatorio

        val caracteresValidos = telefono.all { it.isDigit() || it == '+' || it == ' ' }
        val digitos = telefono.count { it.isDigit() }

        return if (caracteresValidos && digitos in 8..12) null else R.string.val_telefono_invalido
    }
}
