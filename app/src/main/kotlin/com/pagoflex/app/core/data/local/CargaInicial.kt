package com.pagoflex.app.core.data.local

import com.pagoflex.app.core.data.local.entities.Anulacion
import com.pagoflex.app.core.data.local.entities.Area
import com.pagoflex.app.core.data.local.entities.Canal
import com.pagoflex.app.core.data.local.entities.Compromiso
import com.pagoflex.app.core.data.local.entities.Comuna
import com.pagoflex.app.core.data.local.entities.ContactoRecomendacion
import com.pagoflex.app.core.data.local.entities.EmpresaCliente
import com.pagoflex.app.core.data.local.entities.EstadoCompromiso
import com.pagoflex.app.core.data.local.entities.EstadoRecomendacion
import com.pagoflex.app.core.data.local.entities.EstadoReporte
import com.pagoflex.app.core.data.local.entities.HistorialRecomendacion
import com.pagoflex.app.core.data.local.entities.MedioPago
import com.pagoflex.app.core.data.local.entities.MotivoAnulacion
import com.pagoflex.app.core.data.local.entities.MotivoReporte
import com.pagoflex.app.core.data.local.entities.Pago
import com.pagoflex.app.core.data.local.entities.PreguntaRecuperacion
import com.pagoflex.app.core.data.local.entities.Recomendacion
import com.pagoflex.app.core.data.local.entities.Reporte
import com.pagoflex.app.core.data.local.entities.Rol
import com.pagoflex.app.core.data.local.entities.Rubro
import com.pagoflex.app.core.data.local.entities.Usuario
import com.pagoflex.app.core.profiles.model.PerfilUsuario
import com.pagoflex.app.core.security.Hasher
import java.time.LocalDate
import java.time.LocalDateTime

class CargaInicial(
    private val db: LocalDatabase
) {
    // ---------- colecciones ----------
    private val usuariosDemo = listOf(
        "UF-01",
        "AG-01",
        "EJ-01",
        // adonys: ejecutivo del instituto para probar la anulación del arancel (HU-11 CA-3)
        "EJ-06"
    )

    private val preguntasRecuperacion = listOf(
        PreguntaRecuperacion(pregunta = "¿Cuál era el nombre de tu primera mascota?"),
        PreguntaRecuperacion(pregunta = "¿En qué ciudad naciste?"),
        PreguntaRecuperacion(pregunta = "¿Cuál era el nombre de tu escuela primaria?"),
        PreguntaRecuperacion(pregunta = "¿Cuál era tu apodo de infancia?")
    )

    private val roles = listOf(
        Rol(
            codigo = PerfilUsuario.USUARIO_FINAL,
            nombre = "Usuario final",
            descripcion = "Usuario final de la plataforma"
        ),
        Rol(
            codigo = PerfilUsuario.EJECUTIVO,
            nombre = "Ejecutivo de empresa cliente",
            descripcion = "Ejecutivo de empresa cliente de la plataforma"
        ),
        Rol(
            codigo = PerfilUsuario.AGENTE,
            nombre = "Agente de PagoFlex",
            descripcion = "Agente de PagoFlex de la plataforma"
        )
    )

    private val comunas = listOf(
        Comuna(nombre = "Maipú"),
        Comuna(nombre = "Puente Alto"),
        Comuna(nombre = "San Miguel"),
        Comuna(nombre = "La Florida"),
        Comuna(nombre = "Ñuñoa"),
        Comuna(nombre = "Quilicura"),
        Comuna(nombre = "San Bernardo"),
        Comuna(nombre = "Estación Central")
    )

    private val areas = listOf(
        Area(nombre = "Soporte"),
        Area(nombre = "Comercial")
    )

    private val rubros = listOf(
        Rubro(nombre = "Inmobiliaria"),
        Rubro(nombre = "Educación escolar"),
        Rubro(nombre = "Crédito de consumo"),
        Rubro(nombre = "Seguros"),
        Rubro(nombre = "ONG"),
        Rubro(nombre = "Club deportivo"),
        Rubro(nombre = "Educación superior"),
        Rubro(nombre = "Administración de arriendos"),
        Rubro(nombre = "Educación parvularia"),
        Rubro(nombre = "Administración de condominio"),
        Rubro(nombre = "Deporte"),
        Rubro(nombre = "Educación artística")
    )

    private val estadosCompromiso = listOf(
        EstadoCompromiso(nombre = "Pendiente"),
        EstadoCompromiso(nombre = "Vencido"),
        EstadoCompromiso(nombre = "Pagado"),
        EstadoCompromiso(nombre = "En revisión"),
        EstadoCompromiso(nombre = "Anulado")
    )

    private val motivosAnulacion = listOf(
        MotivoAnulacion(nombre = "Repactación"),
        MotivoAnulacion(nombre = "Error de registro"),
        MotivoAnulacion(nombre = "Pagado directamente a la empresa"),
        MotivoAnulacion(nombre = "Otro")
    )

    private val canales = listOf(
        Canal(nombre = "Agente conversacional"),
        Canal(nombre = "Portal de pagos"),
        Canal(nombre = "App")
    )

    private val mediosPago = listOf(
        MedioPago(nombre = "Tarjeta de débito"),
        MedioPago(nombre = "Tarjeta de crédito"),
        MedioPago(nombre = "Transferencia"),
        MedioPago(nombre = "Pago simulado")
    )

    private val motivosReporte = listOf(
        MotivoReporte(nombre = "Ya pagué por otro medio"),
        MotivoReporte(nombre = "El monto no corresponde"),
        MotivoReporte(nombre = "No reconozco este cobro"),
        MotivoReporte(nombre = "Otro")
    )

    private val estadosReporte = listOf(
        EstadoReporte(nombre = "En gestión"),
        EstadoReporte(nombre = "Resuelto"),
        EstadoReporte(nombre = "Abierto")
    )

    private val estadosRecomendacion = listOf(
        EstadoRecomendacion(nombre = "Recibida", orden = 1),
        EstadoRecomendacion(nombre = "Contactada", orden = 2),
        EstadoRecomendacion(nombre = "En evaluación", orden = 3),
        EstadoRecomendacion(nombre = "Se unió", orden = 4),
        EstadoRecomendacion(nombre = "Descartada", orden = 4)
    )

    private val empresasClientes = listOf(
        EmpresaCliente(
            codigo = "EC-01",
            razonSocial = "Inmobiliaria Altos del Maipo SpA",
            nombreFantasia = "Altos del Maipo",
            rut = "76.845.213-k",
            idRubro = 1,
            correoAtencion = "atencion@altosmaipo.ejemplo",
            telefonoAtencion = "+56200000201",
            recargoAtraso = 8_500,
            activa = true
        ),
        EmpresaCliente(
            codigo = "EC-02",
            razonSocial = "Corporación Educacional Bosque Nativo",
            nombreFantasia = "Colegio Bosque Nativo",
            rut = "65.132.874-8",
            idRubro = 2,
            correoAtencion = "pagos@bosquenativo.ejemplo",
            telefonoAtencion = "+56200000202",
            recargoAtraso = 5_000,
            activa = true
        ),
        EmpresaCliente(
            codigo = "EC-03",
            razonSocial = "Crédito Andino S.A.",
            nombreFantasia = "Crédito Andino",
            rut = "96.571.438-3",
            idRubro = 3,
            correoAtencion = "clientes@creditoandino.ejemplo",
            telefonoAtencion = "+56200000203",
            recargoAtraso = 12_000,
            activa = true
        ),
        EmpresaCliente(
            codigo = "EC-04",
            razonSocial = "Seguros Horizonte Sur S.A.",
            nombreFantasia = "Horizonte Sur",
            rut = "96.814.205-4",
            idRubro = 4,
            correoAtencion = "contacto@horizontesur.ejemplo",
            telefonoAtencion = "+56200000204",
            recargoAtraso = 3_000,
            activa = true
        ),
        EmpresaCliente(
            codigo = "EC-05",
            razonSocial = "Fundación Manos Abiertas",
            nombreFantasia = "Manos Abiertas",
            rut = "65.247.190-0",
            idRubro = 5,
            correoAtencion = "socios@manosabiertas.ejemplo",
            telefonoAtencion = "+56200000205",
            recargoAtraso = 0,
            activa = true
        ),
        EmpresaCliente(
            codigo = "EC-06",
            razonSocial = "Club Deportivo Los Halcones",
            nombreFantasia = "Club Los Halcones",
            rut = "65.398.412-k",
            idRubro = 6,
            correoAtencion = "tesoreria@loshalcones.ejemplo",
            telefonoAtencion = "+56200000206",
            recargoAtraso = 2_000,
            activa = true
        ),
        EmpresaCliente(
            codigo = "EC-07",
            razonSocial = "Instituto Técnico Cordillera SpA",
            nombreFantasia = "Instituto Cordillera",
            rut = "77.214.583-7",
            idRubro = 7,
            correoAtencion = "finanzas@itcordillera.ejemplo",
            telefonoAtencion = "+56200000207",
            recargoAtraso = 6_000,
            activa = true
        ),
        EmpresaCliente(
            codigo = "EC-08",
            razonSocial = "Arriendos Plaza Oriente Ltda.",
            nombreFantasia = "Plaza Oriente",
            rut = "76.432.871-k",
            idRubro = 8,
            correoAtencion = "cobranza@plazaoriente.ejemplo",
            telefonoAtencion = "+56200000208",
            recargoAtraso = 7_500,
            activa = true
        )
    )

    // ---------- insertar ----------
    private val fechaTecnica = LocalDate.parse("2026-10-06").atStartOfDay()

    private val idsRoles = mutableMapOf<String, Int>()
    private val idsComunas = mutableMapOf<String, Int>()
    private val idsAreas = mutableMapOf<String, Int>()
    private val idsRubros = mutableMapOf<String, Int>()
    private val idsEstadosCompromiso = mutableMapOf<String, Int>()
    private val idsCanales = mutableMapOf<String, Int>()
    private val idsMediosPago = mutableMapOf<String, Int>()
    private val idsMotivosReporte = mutableMapOf<String, Int>()
    private val idsEstadosReporte = mutableMapOf<String, Int>()
    private val idsEstadosRecomendacion = mutableMapOf<String, Int>()
    private val idsEmpresas = mutableMapOf<String, Int>()
    private val idsUsuarios = mutableMapOf<String, Int>()
    private val idsCompromisos = mutableMapOf<String, Int>()
    private val idsRecomendaciones = mutableMapOf<String, Int>()
    private val idsMotivosAnulacion = mutableMapOf<String, Int>()

    suspend fun cargarDatos() {
        val referencias = db.referenciasDao()
        val idsPreguntas = db.preguntaRecuperacionDao()
            .insertarPreguntas(preguntasRecuperacion)

        // 1. insertar las tablas de referencia y conservar sus ids generados
        val nuevosIdsRoles = referencias.insertarRoles(roles)
        roles.forEachIndexed { indice, rol ->
            idsRoles[rol.codigo.name] = nuevosIdsRoles[indice].toInt()
        }

        val nuevosIdsComunas = referencias.insertarComunas(comunas)
        comunas.forEachIndexed { indice, comuna ->
            idsComunas[comuna.nombre] = nuevosIdsComunas[indice].toInt()
        }

        val nuevosIdsAreas = referencias.insertarAreas(areas)
        areas.forEachIndexed { indice, area ->
            idsAreas[area.nombre] = nuevosIdsAreas[indice].toInt()
        }

        val nuevosIdsRubros = referencias.insertarRubros(rubros)
        rubros.forEachIndexed { indice, rubro ->
            idsRubros[rubro.nombre] = nuevosIdsRubros[indice].toInt()
        }

        val nuevosIdsEstadosCompromiso = referencias.insertarEstadoCompromisos(estadosCompromiso)
        estadosCompromiso.forEachIndexed { indice, estado ->
            idsEstadosCompromiso[estado.nombre] = nuevosIdsEstadosCompromiso[indice].toInt()
        }

        val nuevosIdsMotivosAnulacion =
            referencias.insertarMotivoAnulacions(motivosAnulacion)

        motivosAnulacion.forEachIndexed { indice, motivo ->
            idsMotivosAnulacion[motivo.nombre] =
                nuevosIdsMotivosAnulacion[indice].toInt()
        }

        val nuevosIdsCanales = referencias.insertarCanales(canales)
        canales.forEachIndexed { indice, canal ->
            idsCanales[canal.nombre] = nuevosIdsCanales[indice].toInt()
        }

        val nuevosIdsMediosPago = referencias.insertarMedioPagos(mediosPago)
        mediosPago.forEachIndexed { indice, medio ->
            idsMediosPago[medio.nombre] = nuevosIdsMediosPago[indice].toInt()
        }

        val nuevosIdsMotivosReporte = referencias.insertarMotivoReportes(motivosReporte)
        motivosReporte.forEachIndexed { indice, motivo ->
            idsMotivosReporte[motivo.nombre] = nuevosIdsMotivosReporte[indice].toInt()
        }

        val nuevosIdsEstadosReporte = referencias.insertarEstadoReportes(estadosReporte)
        estadosReporte.forEachIndexed { indice, estado ->
            idsEstadosReporte[estado.nombre] = nuevosIdsEstadosReporte[indice].toInt()
        }

        val nuevosIdsEstadosRecomendacion =
            referencias.insertarEstadoRecomendacions(estadosRecomendacion)
        estadosRecomendacion.forEachIndexed { indice, estado ->
            idsEstadosRecomendacion[estado.nombre] = nuevosIdsEstadosRecomendacion[indice].toInt()
        }

        // 2. asociar cada empresa con él id real de su rubro
        val empresasConRubros = empresasClientes.map { empresa ->
            val indiceRubro = empresa.idRubro - 1
            val nombreRubro = rubros[indiceRubro].nombre
            empresa.copy(idRubro = obtenerIdRubro(nombreRubro))
        }
        val nuevosIdsEmpresas = db.empresaClienteDao().insertarEmpresasCliente(empresasConRubros)
        empresasConRubros.forEachIndexed { indice, empresa ->
            idsEmpresas[empresa.codigo] = nuevosIdsEmpresas[indice].toInt()
        }

        // 3. insertar usuarios por perfil
        insertarUsuarioFinal(
            "UF-01",
            "Camila Andrea",
            "Rojas Peña",
            "15.834.207-3",
            "crojas@correo.ejemplo",
            telefono = "+56 9 0000 0101",
            comuna = "Maipú",
            incorporacion = "2023-03-14",
            diasAviso = 3
        )
        insertarUsuarioFinal(
            "UF-02",
            "Luis Alberto",
            "Muñoz Tapia",
            "12.476.318-5",
            "lmunoz@correo.ejemplo",
            telefono = "+56 9 0000 0102",
            comuna = "Puente Alto",
            incorporacion = "2023-11-02",
            diasAviso = 1
        )
        insertarUsuarioFinal(
            "UF-03",
            "Rosa Elena",
            "Contreras Díaz",
            "6.893.541-5",
            "rcontreras@correo.ejemplo",
            telefono = "+56 9 0000 0103",
            comuna = "San Miguel",
            incorporacion = "2024-06-20",
            diasAviso = 5
        )
        insertarUsuarioFinal(
            "UF-04",
            "Matías Ignacio",
            "Soto Vergara",
            "21.045.873-5",
            "msoto@correo.ejemplo",
            telefono = "+56 9 0000 0104",
            comuna = "La Florida",
            incorporacion = "2025-03-07"
        )
        insertarUsuarioFinal(
            "UF-05",
            "Fernanda Paz",
            "Godoy Lagos",
            "17.562.904-1",
            "fgodoy@correo.ejemplo",
            telefono = "+56 9 0000 0105",
            comuna = "Ñuñoa",
            incorporacion = "2024-01-15",
            diasAviso = 3
        )
        insertarUsuarioFinal(
            "UF-06",
            "Héctor Manuel",
            "Pizarro Campos",
            "10.318.265-4",
            "hpizarro@correo.ejemplo",
            telefono = "+56 9 0000 0106",
            comuna = "Quilicura",
            incorporacion = "2023-08-28",
            diasAviso = 3
        )
        insertarUsuarioFinal(
            "UF-07",
            "Javiera Constanza",
            "Reyes Molina",
            "19.287.430-0",
            "jreyes@correo.ejemplo",
            telefono = "+56 9 0000 0107",
            comuna = "San Bernardo",
            incorporacion = "2025-05-03",
            diasAviso = 1
        )
        insertarUsuarioFinal(
            "UF-08",
            "Benjamín Esteban",
            "Araya Fuentes",
            "18.704.396-4",
            "baraya@correo.ejemplo",
            telefono = "+56 9 0000 0108",
            comuna = "Estación Central",
            incorporacion = "2026-09-11",
            diasAviso = 3
        )

        insertarAgente(
            "AG-01",
            "Andrés Felipe",
            "Lagos Meza",
            "16.248.317-K",
            "alagos@pagoflex.ejemplo",
            area = "Soporte"
        )
        insertarAgente(
            "AG-02",
            "Daniela Sofía",
            "Ortiz Bravo",
            "18.352.091-1",
            "dortiz@pagoflex.ejemplo",
            area = "Soporte"
        )
        insertarAgente(
            "AG-03",
            "Ricardo José",
            "Salinas Cáceres",
            "13.907.264-2",
            "rsalinas@pagoflex.ejemplo",
            area = "Soporte",
            activo = false
        )
        insertarAgente(
            "AG-04",
            "Valentina Ignacia",
            "Paredes Silva",
            "17.781.435-0",
            "vparedes@pagoflex.ejemplo",
            area = "Comercial"
        )
        insertarAgente(
            "AG-05",
            "Tomás Alejandro",
            "Vidal Rojas",
            "19.564.028-9",
            "tvidal@pagoflex.ejemplo",
            area = "Comercial"
        )

        insertarEjecutivo(
            "EJ-01",
            "Paula Andrea",
            "Saavedra Núñez",
            "14.629.853-2",
            "psaavedra@altosmaipo.ejemplo",
            telefono = "+56 9 0000 0401",
            empresa = "EC-01",
            cargo = "Ejecutiva de cobranza"
        )
        insertarEjecutivo(
            "EJ-02",
            "Marco Antonio",
            "Fuentes Riquelme",
            "11.853.720-3",
            "mfuentes@bosquenativo.ejemplo",
            telefono = "+56 9 0000 0402",
            empresa = "EC-02",
            cargo = "Encargado de finanzas"
        )
        insertarEjecutivo(
            "EJ-03",
            "Claudia Beatriz",
            "Henríquez Soto",
            "15.370.946-7",
            "chenriquez@creditoandino.ejemplo",
            telefono = "+56 9 0000 0403",
            empresa = "EC-03",
            cargo = "Jefa de cobranza"
        )
        insertarEjecutivo(
            "EJ-04",
            "Felipe Ignacio",
            "Morales Tapia",
            "17.095.482-3",
            "fmorales@creditoandino.ejemplo",
            telefono = "+56 9 0000 0404",
            empresa = "EC-03",
            cargo = "Ejecutivo de cobranza"
        )
        insertarEjecutivo(
            "EJ-05",
            "Gloria Inés",
            "Carrasco Vega",
            "9.846.217-1",
            "gcarrasco@loshalcones.ejemplo",
            telefono = "+56 9 0000 0405",
            empresa = "EC-06",
            cargo = "Tesorera"
        )
        insertarEjecutivo(
            "EJ-06",
            "Sebastián Andrés",
            "Núñez Olivares",
            "18.930.571-0",
            "snunez@itcordillera.ejemplo",
            telefono = "+56 9 0000 0406",
            empresa = "EC-07",
            cargo = "Analista de finanzas"
        )
        insertarEjecutivo(
            "EJ-07",
            "Mónica Patricia",
            "Leiva Castro",
            "12.584.396-4",
            "mleiva@plazaoriente.ejemplo",
            telefono = "+56 9 0000 0407",
            empresa = "EC-08",
            cargo = "Administradora de cobranza"
        )

        // 4. insertar compromisos y sus relaciones
        insertarCompromisoInicial(
            folio = "CP-2026-0101", codigoUsuario = "UF-01", codigoEmpresa = "EC-08",
            concepto = "Arriendo departamento octubre", cuota = 10, totalCuotas = 12,
            monto = 520000, vencimiento = "2026-10-10", estado = "Pendiente",
            ejecutivo = "EJ-07"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0102", codigoUsuario = "UF-01", codigoEmpresa = "EC-06",
            concepto = "Cuota social octubre", cuota = 10, totalCuotas = 12,
            monto = 15000, vencimiento = "2026-10-05", estado = "Vencido",
            recargo = 2000, ejecutivo = "EJ-05"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0103", codigoUsuario = "UF-01", codigoEmpresa = "EC-05",
            concepto = "Aporte mensual octubre", cuota = null, totalCuotas = null,
            monto = 10000, vencimiento = "2026-10-15", estado = "Pendiente"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0104", codigoUsuario = "UF-01", codigoEmpresa = "EC-03",
            concepto = "Crédito de consumo", cuota = 7, totalCuotas = 24,
            monto = 86400, vencimiento = "2026-09-25", estado = "Pagado",
            ejecutivo = "EJ-03", fechaCambio = "2026-09-24T10:42"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0105", codigoUsuario = "UF-01", codigoEmpresa = "EC-03",
            concepto = "Crédito de consumo", cuota = 8, totalCuotas = 24,
            monto = 86400, vencimiento = "2026-10-25", estado = "Pendiente",
            ejecutivo = "EJ-03"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0106", codigoUsuario = "UF-02", codigoEmpresa = "EC-01",
            concepto = "Cuota pie departamento", cuota = 14, totalCuotas = 36,
            monto = 310000, vencimiento = "2026-09-30", estado = "En revisión",
            recargo = 8500,
            ejecutivo = "EJ-01",
            fechaCambio = "2026-10-05T00:00"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0107", codigoUsuario = "UF-02", codigoEmpresa = "EC-01",
            concepto = "Cuota pie departamento", cuota = 15, totalCuotas = 36,
            monto = 310000, vencimiento = "2026-10-30", estado = "Pendiente",
            ejecutivo = "EJ-01"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0108", codigoUsuario = "UF-03", codigoEmpresa = "EC-04",
            concepto = "Seguro de hogar octubre", cuota = 10, totalCuotas = 12,
            monto = 18900, vencimiento = "2026-10-01", estado = "Pagado",
            fechaCambio = "2026-09-30T19:05"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0109", codigoUsuario = "UF-04", codigoEmpresa = "EC-07",
            concepto = "Arancel octubre", cuota = 8, totalCuotas = 10,
            monto = 142000, vencimiento = "2026-10-05", estado = "En revisión",
            ejecutivo = "EJ-06", fechaCambio = "2026-10-03T00:00"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0110", codigoUsuario = "UF-05", codigoEmpresa = "EC-02",
            concepto = "Mensualidad octubre", cuota = 8, totalCuotas = 10,
            monto = 185000, vencimiento = "2026-10-10", estado = "Pendiente",
            ejecutivo = "EJ-02"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0111", codigoUsuario = "UF-06", codigoEmpresa = "EC-03",
            concepto = "Crédito de consumo", cuota = 19, totalCuotas = 36,
            monto = 54200, vencimiento = "2026-09-20", estado = "Anulado",
            ejecutivo = "EJ-04", fechaCambio = "2026-09-22T00:00"
        )
        insertarCompromisoInicial(
            folio = "CP-2026-0112", codigoUsuario = "UF-07", codigoEmpresa = "EC-06",
            concepto = "Cuota social octubre", cuota = 10, totalCuotas = 12,
            monto = 15000, vencimiento = "2026-10-05", estado = "Pagado",
            ejecutivo = "EJ-05", fechaCambio = "2026-10-04T08:15"
        )

        // 5. insertar anulaciones asociadas a los compromisos
        insertarAnulacionInicial(
            folio = "CP-2026-0111",
            ejecutivo = "EJ-04",
            motivo = "Repactación",
            fecha = "2026-09-22"
        )
        // 6. insertar pagos y reportes asociados
        insertarPagoInicial(
            "CPR-000451",
            "CP-2026-0104",
            "2026-09-24T10:42",
            86400,
            "Portal de pagos",
            "Tarjeta de débito"
        )
        insertarPagoInicial(
            "CPR-000452",
            "CP-2026-0108",
            "2026-09-30T19:05",
            18900,
            "Agente conversacional",
            "Pago simulado"
        )
        insertarPagoInicial(
            "CPR-000453",
            "CP-2026-0112",
            "2026-10-04T08:15",
            15000,
            "App",
            "Pago simulado"
        )
        insertarReporteInicial(
            "REP-0011",
            "CP-2026-0109",
            "Ya pagué por otro medio",
            "Pagué por transferencia directa al instituto el 03-10.",
            "2026-10-03",
            "En gestión",
            agente = "AG-02"
        )
        insertarReporteInicial(
            "REP-0009",
            "CP-2026-0111",
            "El monto no corresponde",
            "Repacté el crédito y me siguen cobrando la cuota antigua.",
            "2026-09-18",
            "Resuelto",
            agente = "AG-01",
            respuesta = "La empresa anuló la cuota por repactación (22-09-2026).",
            resolucion = "2026-09-22"
        )
        insertarReporteInicial(
            "REP-0012",
            "CP-2026-0106",
            "El monto no corresponde",
            "No sé por qué me cobran $318.500.",
            "2026-10-05",
            "Abierto"
        )


        // 7. insertar recomendaciones y contactos asociados
        insertarRecomendacionInicial(
            "REC-0001",
            "UF-01",
            "Jardín Infantil Pequeños Exploradores",
            "Educación parvularia",
            "Maipú",
            "2026-08-12",
            "Se unió"
        )
        insertarRecomendacionInicial(
            "REC-0002",
            "UF-01",
            "Comunidad Edificio Parque Los Olmos",
            "Administración de condominio",
            "Maipú",
            "2026-09-02",
            "Contactada"
        )
        insertarRecomendacionInicial(
            "REC-0003",
            "UF-03",
            "Gimnasio Fuerza Sur",
            "Deporte",
            "San Miguel",
            "2026-09-15",
            "En evaluación"
        )
        insertarRecomendacionInicial(
            "REC-0004",
            "UF-05",
            "Academia de Música Clave de Sol",
            "Educación artística",
            "Ñuñoa",
            "2026-09-20",
            "Recibida"
        )
        insertarRecomendacionInicial(
            "REC-0005",
            "UF-04",
            "Colegio Bosque Nativo",
            "Educación escolar",
            "La Florida",
            "2026-09-22",
            "Descartada"
        )
        insertarRecomendacionInicial(
            "REC-0006",
            "UF-07",
            "Corporación Deportiva Cordillera",
            "Club deportivo",
            "San Bernardo",
            "2026-10-01",
            "Recibida"
        )
        insertarContactoInicial(
            "REC-0001",
            "Paula Herrera",
            correo = "pherrera@pexploradores.ejemplo"
        )
        insertarContactoInicial("REC-0003", "Raúl Ibáñez", telefono = "+56 9 0000 0301")
        insertarContactoInicial("REC-0006", "Ana Valdés", correo = "avaldes@cdcordillera.ejemplo")

        // 8. insertar últimos cambios de estado de las recomendaciones
        insertarHistorialRecomendacionInicial(
            numero = "REC-0001",
            estado = "Se unió",
            agente = "AG-04",
            fecha = "2026-09-25"
        )

        insertarHistorialRecomendacionInicial(
            numero = "REC-0002",
            estado = "Contactada",
            agente = "AG-05",
            fecha = "2026-09-10"
        )

        insertarHistorialRecomendacionInicial(
            numero = "REC-0003",
            estado = "En evaluación",
            agente = "AG-04",
            fecha = "2026-09-28"
        )

        insertarHistorialRecomendacionInicial(
            numero = "REC-0005",
            estado = "Descartada",
            agente = "AG-05",
            fecha = "2026-09-23",
            motivo = "La organización ya es cliente de PagoFlex"
        )

        // 9. configurar acceso a usuarios demo
        usuariosDemo.forEach { cod ->
            db.usuarioDao().configurarAccesoInicial(
                usuarioId = obtenerIdUsuario(cod),
                passwordHash = Hasher.genHash("demo123"),
                idPregunta = idsPreguntas.first().toInt(),
                respuestaHash = Hasher.genHash("respuesta-demo")
            )
        }

        // 10. adonys: completar la cobranza de septiembre de altos del maipo (HU-12)
        CargaCobranzaSeptiembre(db).cargarDatos()
    }

    // ---------- helpers privados ----------
    private fun obtenerIdRol(perfil: PerfilUsuario): Int =
        requireNotNull(idsRoles[perfil.name]) { "No existe el rol ${perfil.name}" }

    private fun obtenerIdComuna(nombre: String): Int =
        requireNotNull(idsComunas[nombre]) { "No existe la comuna $nombre" }

    private fun obtenerIdArea(nombre: String): Int =
        requireNotNull(idsAreas[nombre]) { "No existe el area $nombre" }

    private fun obtenerIdRubro(nombre: String): Int =
        requireNotNull(idsRubros[nombre]) { "No existe el rubro $nombre" }

    private fun obtenerIdEstadoCompromiso(nombre: String): Int =
        requireNotNull(idsEstadosCompromiso[nombre]) { "No existe el estado de compromiso $nombre" }

    private fun obtenerIdMotivoAnulacion(nombre: String): Int =
        requireNotNull(idsMotivosAnulacion[nombre]) {
            "No existe el motivo de anulacion $nombre"
        }

    private fun obtenerIdCanal(nombre: String): Int =
        requireNotNull(idsCanales[nombre]) { "No existe el canal $nombre" }

    private fun obtenerIdMedioPago(nombre: String): Int =
        requireNotNull(idsMediosPago[nombre]) { "No existe el medio de pago $nombre" }

    private fun obtenerIdMotivoReporte(nombre: String): Int =
        requireNotNull(idsMotivosReporte[nombre]) { "No existe el motivo de reporte $nombre" }

    private fun obtenerIdEstadoReporte(nombre: String): Int =
        requireNotNull(idsEstadosReporte[nombre]) { "No existe el estado de reporte $nombre" }

    private fun obtenerIdEstadoRecomendacion(nombre: String): Int =
        requireNotNull(idsEstadosRecomendacion[nombre]) { "No existe el estado de recomendacion $nombre" }

    private fun obtenerIdEmpresa(codigo: String): Int =
        requireNotNull(idsEmpresas[codigo]) { "No existe la empresa $codigo" }

    private fun obtenerIdUsuario(codigo: String): Int =
        requireNotNull(idsUsuarios[codigo]) { "No existe el usuario $codigo" }

    private fun obtenerIdCompromiso(folio: String): Int =
        requireNotNull(idsCompromisos[folio]) { "No existe el compromiso $folio" }

    private fun obtenerIdRecomendacion(numero: String): Int =
        requireNotNull(idsRecomendaciones[numero]) { "No existe la recomendacion $numero" }

    private suspend fun insertarUsuarioFinal(
        codigo: String, nombres: String, apellidos: String, rut: String, correo: String,
        telefono: String, comuna: String, incorporacion: String, diasAviso: Int? = null
    ) {
        // 1. construir el usuario con sus campos específicos
        val fechaIncorporacion = LocalDate.parse(incorporacion)
        val usuario = Usuario(
            idRol = obtenerIdRol(PerfilUsuario.USUARIO_FINAL),
            nombres = nombres, apellidos = apellidos, rut = rut, correo = correo,
            telefono = telefono,
            fechaCreacion = fechaIncorporacion.atStartOfDay(),
            idComuna = obtenerIdComuna(comuna),
            fechaIncorporacion = fechaIncorporacion,
            avisosActivos = diasAviso != null,
            diasAnticipacion = diasAviso
        )
        // 2. insertar y conservar su id para otras tablas
        idsUsuarios[codigo] = db.usuarioDao().insertarUsuario(usuario).toInt()
    }

    private suspend fun insertarAgente(
        codigo: String, nombres: String, apellidos: String, rut: String,
        correo: String, area: String, activo: Boolean = true
    ) {
        // 1. construir el agente con su área
        val agente = Usuario(
            idRol = obtenerIdRol(PerfilUsuario.AGENTE),
            nombres = nombres, apellidos = apellidos, rut = rut, correo = correo,
            activo = activo, fechaCreacion = fechaTecnica,
            idArea = obtenerIdArea(area)
        )
        // 2. insertar y conservar su id para otras tablas
        idsUsuarios[codigo] = db.usuarioDao().insertarUsuario(agente).toInt()
    }

    private suspend fun insertarEjecutivo(
        codigo: String, nombres: String, apellidos: String, rut: String,
        correo: String, telefono: String, empresa: String, cargo: String
    ) {
        // 1. construir el ejecutivo asociado a su empresa
        val ejecutivo = Usuario(
            idRol = obtenerIdRol(PerfilUsuario.EJECUTIVO),
            nombres = nombres, apellidos = apellidos, rut = rut, correo = correo,
            telefono = telefono, fechaCreacion = fechaTecnica,
            idEmpresa = obtenerIdEmpresa(empresa), cargo = cargo
        )
        // 2. insertar y conservar su id para otras tablas
        idsUsuarios[codigo] = db.usuarioDao().insertarUsuario(ejecutivo).toInt()
    }

    private suspend fun insertarCompromisoInicial(
        folio: String, codigoUsuario: String, codigoEmpresa: String, concepto: String,
        cuota: Int?, totalCuotas: Int?, monto: Int, vencimiento: String,
        estado: String, recargo: Int = 0, ejecutivo: String? = null,
        fechaCambio: String? = null
    ) {
        // 1. resolver el usuario, la empresa y el estado del compromiso
        val compromiso = Compromiso(
            idCompromiso = 0,
            folio = folio,
            idUsuarioFinal = obtenerIdUsuario(codigoUsuario),
            idEmpresa = obtenerIdEmpresa(codigoEmpresa),
            concepto = concepto,
            numeroCuota = cuota,
            totalCuotas = totalCuotas,
            monto = monto,
            fechaVencimiento = LocalDate.parse(vencimiento),
            idEstadoCompromiso = obtenerIdEstadoCompromiso(estado),
            recargoAplicado = recargo,
            fechaCambioEstado = fechaCambio?.let(LocalDateTime::parse) ?: fechaTecnica,
            idEjecutivoRegistro = ejecutivo?.let { obtenerIdUsuario(it) },
            fechaRegistro = LocalDate.parse(vencimiento).minusMonths(1).atStartOfDay()
        )
        // 2. insertar y conservar el id asociado al folio
        idsCompromisos[folio] = db.compromisoDao().insertarCompromiso(compromiso).toInt()
    }

    private suspend fun insertarPagoInicial(
        numero: String, compromiso: String, fecha: String,
        monto: Int, canal: String, medio: String
    ) {
        // 1. insertar un pago vinculado a su compromiso
        db.pagoDao().procesarPago(
            Pago(
                idPago = 0,
                numeroComprobante = numero,
                idCompromiso = obtenerIdCompromiso(compromiso),
                fechaHora = LocalDateTime.parse(fecha),
                montoPagado = monto,
                idCanal = obtenerIdCanal(canal),
                idMedioPago = obtenerIdMedioPago(medio)
            )
        )
    }

    private suspend fun insertarReporteInicial(
        numero: String, compromiso: String, motivo: String, comentario: String,
        fecha: String, estado: String, agente: String? = null,
        respuesta: String? = null, resolucion: String? = null
    ) {
        // 1. insertar el reporte con sus referencias y resultado si existe
        db.reporteDao().insertarReporte(
            Reporte(
                idReporte = 0,
                numero = numero,
                idCompromiso = obtenerIdCompromiso(compromiso),
                idMotivoReporte = obtenerIdMotivoReporte(motivo),
                comentario = comentario,
                fechaCreacion = LocalDate.parse(fecha).atStartOfDay(),
                idEstadoReporte = obtenerIdEstadoReporte(estado),
                idAgente = agente?.let { obtenerIdUsuario(it) },
                respuesta = respuesta,
                fechaResolucion = resolucion?.let { LocalDate.parse(it).atStartOfDay() }
            ))
    }

    private suspend fun insertarRecomendacionInicial(
        numero: String, usuario: String, organizacion: String, rubro: String,
        comuna: String, fecha: String, estado: String
    ) {
        // 1. insertar la recomendacion con los ids correspondientes
        val recomendacion = Recomendacion(
            idRecomendacion = 0,
            numero = numero,
            idUsuarioFinal = obtenerIdUsuario(usuario),
            nombreOrganizacion = organizacion,
            idRubro = obtenerIdRubro(rubro),
            idComuna = obtenerIdComuna(comuna),
            fecha = LocalDate.parse(fecha).atStartOfDay(),
            idEstadoRecomendacion = obtenerIdEstadoRecomendacion(estado)
        )
        idsRecomendaciones[numero] =
            db.recomendacionDao().insertarRecomendacion(recomendacion).toInt()
    }

    private suspend fun insertarContactoInicial(
        numero: String, nombre: String, correo: String? = null, telefono: String? = null
    ) {
        // 1. insertar el contacto autorizado de la recomendacion
        db.recomendacionDao().insertarContactoRecomendacion(
            ContactoRecomendacion(
                idRecomendacion = obtenerIdRecomendacion(numero),
                nombre = nombre,
                correo = correo,
                telefono = telefono,
                autorizaContacto = true
            )
        )
    }

    private suspend fun insertarAnulacionInicial(
        folio: String,
        ejecutivo: String,
        motivo: String,
        fecha: String
    ) {
        val anulacion = Anulacion(
            idCompromiso = obtenerIdCompromiso(folio),
            idEjecutivo = obtenerIdUsuario(ejecutivo),
            idMotivoAnulacion = obtenerIdMotivoAnulacion(motivo),
            fecha = LocalDate.parse(fecha).atStartOfDay()
        )

        db.anulacionDao().insertarAnulacion(anulacion)
    }

    private suspend fun insertarHistorialRecomendacionInicial(
        numero: String,
        estado: String,
        agente: String,
        fecha: String,
        motivo: String? = null
    ) {
        val historial = HistorialRecomendacion(
            idHistorial = 0,
            idRecomendacion = obtenerIdRecomendacion(numero),
            idEstadoRecomendacion = obtenerIdEstadoRecomendacion(estado),
            idAgente = obtenerIdUsuario(agente),
            fecha = LocalDate.parse(fecha).atStartOfDay(),
            motivo = motivo
        )

        db.recomendacionDao().insertarHistorialRecomendacion(historial)
    }
}
