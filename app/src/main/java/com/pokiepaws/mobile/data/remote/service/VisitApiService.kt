package com.pokiepaws.mobile.data.remote.service

import com.pokiepaws.mobile.data.remote.dto.visit.CreateVisitRequest
import com.pokiepaws.mobile.data.remote.dto.visit.VisitResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VisitApiService {
    @GET("api/owners/me/visits/upcoming")
    suspend fun getUpcomingOwnerVisits(): List<VisitResponse>

    @GET("api/owners/me/visits")
    suspend fun getMyVisitsInRange(
        @Query("from") from: String,
        @Query("to") to: String,
    ): List<VisitResponse>

    @GET("api/animals/{animalId}/visits")
    suspend fun getVisitsByAnimal(
        @Path("animalId") animalId: Long,
    ): List<VisitResponse>

    @POST("api/visits")
    suspend fun createVisit(
        @Body request: CreateVisitRequest,
    ): VisitResponse

    @GET("api/visits/{id}")
    suspend fun getVisitById(
        @Path("id") visitId: Long,
    ): VisitResponse

    @PATCH("api/visits/{id}/cancel")
    suspend fun cancelVisit(
        @Path("id") visitId: Long,
    ): VisitResponse
}
