package com.pagoflex.app.features.home.data

import com.pagoflex.app.core.network.RetrofitClient
import com.pagoflex.app.features.home.data.remote.dto.response.MiCompromisoResponse
import com.pagoflex.app.features.home.data.remote.HomeApi
import com.pagoflex.app.features.home.data.remote.dto.response.CumplimientoHomeResponse
import com.pagoflex.app.features.home.data.remote.dto.response.PerfilHomeResponse
import com.pagoflex.app.features.home.data.remote.dto.response.ResumenHomeResponse
import retrofit2.create
import java.time.YearMonth

/** Consultas autenticadas para Inicio; los importes y estados provienen del servidor. */
class HomeRepository {
    private val api = RetrofitClient.retrofit.create<HomeApi>()

    suspend fun obtenerCabecera(): PerfilHomeResponse = api.perfil()

    suspend fun obtenerAvatar(): ByteArray = api.avatar().use { it.bytes() }

    suspend fun obtenerResumen(mes: YearMonth): ResumenHomeResponse = api.resumen(mes.toString())

    suspend fun obtenerCompromisos(): List<MiCompromisoResponse> = api.compromisos()

    suspend fun obtenerCumplimiento(): CumplimientoHomeResponse = api.cumplimiento()
}
