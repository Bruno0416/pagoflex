package com.pagoflex.app.features.cobranza.data

import androidx.annotation.StringRes
import com.pagoflex.app.R
import com.pagoflex.app.core.data.remote.CuentaRemota
import com.pagoflex.app.core.rules.MotivoAnulacion
import com.pagoflex.app.core.validators.Validaciones
import com.pagoflex.app.features.cobranza.data.remote.CobranzaRemoto
import com.pagoflex.app.features.cobranza.presentation.model.CampoCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.CompromisoEmpresa
import com.pagoflex.app.features.cobranza.presentation.model.FormularioCompromiso
import com.pagoflex.app.features.cobranza.presentation.model.ReporteDelCompromiso
import java.time.LocalDate
import java.time.LocalDateTime

// ejecutivo en sesión con su empresa
data class EjecutivoSesion(
    val idUsuario: Int,
    val nombre: String,
    val idEmpresa: Int?,
    val empresa: String?,
    // para el avatar de la barra superior
    val iniciales: String = "",
    val avatar: ByteArray? = null,
)

// persona encontrada por rut al registrar
data class PersonaEncontrada(
    val nombre: String,
    val correo: String?,
)

// lo que muestra la confirmación del registro
data class CompromisoRegistrado(
    val folio: String,
    val monto: Long,
    val fechaVencimiento: LocalDate,
)

sealed class ResultadoRegistro {
    data class Registrado(val compromiso: CompromisoRegistrado) : ResultadoRegistro()
    data class ConErrores(
        val errores: Map<CampoCompromiso, Int> = emptyMap(),
        @StringRes val errorGeneral: Int? = null,
    ) : ResultadoRegistro()
}

// gestión de compromisos del ejecutivo: cobranza (HU-12), registro y anulación (HU-11)
// todo va a cobranza-service; la empresa y los permisos los decide el backend con el token
class CobranzaRepository(
    private val remoto: CobranzaRemoto,
    private val cuenta: CuentaRemota,
) {

    companion object {
        // la empresa la conoce el backend; el id local no se usa
        private const val ID_EMPRESA_EN_LINEA = 0
    }

    // solo un ejecutivo con empresa entra a la cobranza (RN-22)
    suspend fun obtenerEjecutivo(usuarioId: Int): EjecutivoSesion? {
        val actual = cuenta.actual() ?: return null
        val empresa = remoto.nombreEmpresa()
        return EjecutivoSesion(
            idUsuario = usuarioId,
            nombre = actual.nombre,
            idEmpresa = empresa?.let { ID_EMPRESA_EN_LINEA },
            empresa = empresa,
            iniciales = actual.iniciales
        )
    }

    // ---------- consultas ----------

    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerCompromisos(empresaId: Int, hoy: LocalDate): List<CompromisoEmpresa> =
        remoto.obtenerCompromisos()

    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerDetalle(empresaId: Int, folio: String, hoy: LocalDate): CompromisoEmpresa? =
        remoto.obtenerDetalle(folio)

    // reporte sin resolver del compromiso, solo lectura y solo de su empresa (RN-22)
    @Suppress("UNUSED_PARAMETER")
    suspend fun obtenerReporteActivo(empresaId: Int, folio: String): ReporteDelCompromiso? =
        remoto.reporteActivo(folio)

    suspend fun buscarPersona(rut: String): PersonaEncontrada? {
        if (Validaciones.validarRut(rut) != null) return null
        return remoto.buscarPersona(rut)
    }

    // ---------- registro (HU-11, RN-20) ----------

    private val camposPersona = listOf(
        CampoCompromiso.NOMBRES, CampoCompromiso.APELLIDOS, CampoCompromiso.CORREO, CampoCompromiso.TELEFONO
    )
    private val camposCompromiso = listOf(
        CampoCompromiso.CONCEPTO, CampoCompromiso.MONTO, CampoCompromiso.VENCIMIENTO, CampoCompromiso.CUOTA
    )

    // validación de un campo, se usa al perder el foco y al registrar
    @StringRes
    fun validarCampo(campo: CampoCompromiso, formulario: FormularioCompromiso, hoy: LocalDate): Int? =
        when (campo) {
            CampoCompromiso.RUT -> Validaciones.validarRut(formulario.rut)
            CampoCompromiso.NOMBRES -> Validaciones.validarObligatorio(formulario.nombres, R.string.val_nombres_obligatorios)
            CampoCompromiso.APELLIDOS -> Validaciones.validarObligatorio(formulario.apellidos, R.string.val_apellidos_obligatorios)
            CampoCompromiso.CORREO -> Validaciones.validarCorreo(formulario.correo)
            CampoCompromiso.TELEFONO -> Validaciones.validarTelefono(formulario.telefono)
            CampoCompromiso.CONCEPTO -> Validaciones.validarObligatorio(formulario.concepto, R.string.val_concepto_obligatorio)
            CampoCompromiso.MONTO -> Validaciones.validarMonto(formulario.monto)
            CampoCompromiso.VENCIMIENTO -> Validaciones.validarFechaVencimiento(formulario.vencimiento, hoy)
            CampoCompromiso.CUOTA -> Validaciones.validarCuota(formulario.numeroCuota, formulario.totalCuotas)
        }

    // mismas validaciones en la app; el duplicado y el folio los resuelve el backend
    @Suppress("UNUSED_PARAMETER")
    suspend fun registrar(
        formulario: FormularioCompromiso,
        ejecutivo: EjecutivoSesion,
        ahora: LocalDateTime,
    ): ResultadoRegistro {
        val hoy = ahora.toLocalDate()
        val errores = mutableMapOf<CampoCompromiso, Int>()

        // 1. persona: buscarla en el backend y pedir datos solo si no existe
        val errorRut = validarCampo(CampoCompromiso.RUT, formulario, hoy)
        errorRut?.let { errores[CampoCompromiso.RUT] = it }
        val personaNueva = errorRut == null && remoto.buscarPersona(formulario.rut) == null
        if (personaNueva) {
            camposPersona.forEach { campo -> validarCampo(campo, formulario, hoy)?.let { errores[campo] = it } }
        }

        // 2. datos del compromiso
        camposCompromiso.forEach { campo -> validarCampo(campo, formulario, hoy)?.let { errores[campo] = it } }

        if (errores.isNotEmpty()) return ResultadoRegistro.ConErrores(errores)
        return remoto.registrar(formulario, personaNueva)
    }

    // ---------- anulación (HU-11, RN-21) ----------

    // devuelve el id del mensaje de error o null si se anuló; empresa y estado los revisa el backend
    @Suppress("UNUSED_PARAMETER")
    suspend fun anular(
        folio: String,
        ejecutivo: EjecutivoSesion,
        motivo: MotivoAnulacion?,
        comentario: String,
        ahora: LocalDateTime,
    ): Int? {
        if (motivo == null) return R.string.cob_error_motivo_obligatorio
        Validaciones.validarComentario(motivo == MotivoAnulacion.OTRO, comentario)?.let { return it }
        return remoto.anular(folio, motivo, comentario)
    }
}
