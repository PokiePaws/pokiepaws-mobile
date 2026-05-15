package com.pokiepaws.mobile.data.remote.service

import com.pokiepaws.mobile.data.remote.dto.vet.VetListResponse
import retrofit2.http.GET
import retrofit2.http.Path

fun interface VetApiService {
    @GET("api/vets/clinic/{clinicId}/list")
    suspend fun getByClinicList(
        @Path("clinicId") clinicId: Long,
    ): List<VetListResponse>
}
