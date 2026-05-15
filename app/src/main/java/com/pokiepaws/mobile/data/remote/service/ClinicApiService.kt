package com.pokiepaws.mobile.data.remote.service

import com.pokiepaws.mobile.data.remote.dto.clinic.ClinicResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ClinicApiService {
    @GET("api/clinics")
    suspend fun getAll(): List<ClinicResponse>

    @GET("api/clinics/{id}")
    suspend fun getById(
        @Path("id") id: Long,
    ): ClinicResponse
}
