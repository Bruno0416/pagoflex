package com.pagoflex.app.core.validators

import com.pagoflex.app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ValidacionesTest {

    private val hoy = LocalDate.of(2026, 10, 6)

    // ---------- rut (RN-18) ----------

    @Test
    fun `los rut del anexo 9 son validos`() {
        val ruts = listOf(
            "15.834.207-3", "12.476.318-5", "6.893.541-5", "21.045.873-5",
            "76.845.213-K", "16.248.317-K", "14.629.853-2", "65.398.412-K",
        )
        ruts.forEach { assertNull("falló $it", Validaciones.validarRut(it)) }
    }

    @Test
    fun `rut con digito verificador incorrecto`() {
        assertEquals(R.string.val_rut_dv, Validaciones.validarRut("15.834.207-4"))
    }

    @Test
    fun `rut acepta con y sin puntos y k minuscula`() {
        assertNull(Validaciones.validarRut("158342073"))
        assertNull(Validaciones.validarRut("76845213-k"))
    }

    @Test
    fun `rut vacio o con letras`() {
        assertEquals(R.string.val_rut_obligatorio, Validaciones.validarRut(""))
        assertEquals(R.string.val_rut_formato, Validaciones.validarRut("15.8A4.207-3"))
    }

    @Test
    fun `formatear rut igual que el registro`() {
        assertEquals("15.834.207-3", Validaciones.formatearRut("158342073"))
        assertEquals("76.845.213-K", Validaciones.formatearRut("76845213k"))
    }

    // ---------- montos (RN-17) ----------

    @Test
    fun `monto entero mayor que cero`() {
        assertNull(Validaciones.validarMonto("310000"))
        assertNull(Validaciones.validarMonto("$ 310.000"))
        assertEquals(310_000L, Validaciones.parsearMonto("310.000"))
    }

    @Test
    fun `monto cero negativo o con decimales`() {
        assertEquals(R.string.val_monto_mayor_cero, Validaciones.validarMonto("0"))
        assertEquals(R.string.val_monto_entero, Validaciones.validarMonto("-5000"))
        assertEquals(R.string.val_monto_entero, Validaciones.validarMonto("12,5"))
        assertEquals(R.string.val_monto_obligatorio, Validaciones.validarMonto(""))
    }

    // ---------- fechas (RN-17, RN-20) ----------

    @Test
    fun `fecha en formato DD-MM-AAAA`() {
        assertNotNull(Validaciones.parsearFecha("30-11-2026"))
        assertNull(Validaciones.parsearFecha("2026-11-30"))
        assertNull(Validaciones.parsearFecha("31-02-2026"))
    }

    @Test
    fun `vencimiento no puede ser anterior a hoy`() {
        assertNull(Validaciones.validarFechaVencimiento("06-10-2026", hoy))
        assertEquals(R.string.val_fecha_pasada, Validaciones.validarFechaVencimiento("05-10-2026", hoy))
        assertEquals(R.string.val_fecha_formato, Validaciones.validarFechaVencimiento("2026-10-06", hoy))
    }

    // ---------- cuotas (RN-20) ----------

    @Test
    fun `cuota no puede ser mayor que el total`() {
        assertNull(Validaciones.validarCuota("16", "36"))
        assertNull(Validaciones.validarCuota("", ""))
        assertEquals(R.string.val_cuota_mayor_total, Validaciones.validarCuota("37", "36"))
        assertEquals(R.string.val_cuota_incompleta, Validaciones.validarCuota("16", ""))
        assertEquals(R.string.val_cuota_mayor_cero, Validaciones.validarCuota("0", "36"))
    }

    // ---------- textos ----------

    @Test
    fun `comentario obligatorio solo con motivo otro`() {
        assertEquals(R.string.val_comentario_otro, Validaciones.validarComentario(true, " "))
        assertNull(Validaciones.validarComentario(true, "Detalle"))
        assertNull(Validaciones.validarComentario(false, ""))
    }

    @Test
    fun `correo y telefono`() {
        assertNull(Validaciones.validarCorreo("lmunoz@correo.ejemplo"))
        assertEquals(R.string.val_correo_invalido, Validaciones.validarCorreo("lmunoz@"))
        assertNull(Validaciones.validarTelefono("+56 9 0000 0102"))
        assertEquals(R.string.val_telefono_invalido, Validaciones.validarTelefono("12-34"))
    }
}
