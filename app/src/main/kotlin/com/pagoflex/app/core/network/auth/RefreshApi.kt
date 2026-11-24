package com.pagoflex.app.core.network.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {
    @POST("auth/refresh")
    fun refrescar(@Body request: RefreshRequest): Call<RefreshResponse>
}
