package com.pokiepaws.mobile.data.remote.service

import com.pokiepaws.mobile.data.remote.dto.clinic.ClinicResponse
import retrofit2.http.GET

interface ClinicApiService {
    @GET("api/clinics")
    suspend fun getAll(): List<ClinicResponse>
}
