package com.pokiepaws.mobile.data.remote.service

import com.pokiepaws.mobile.data.remote.dto.animal.AnimalRequest
import com.pokiepaws.mobile.data.remote.dto.animal.AnimalResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AnimalApiService {
    @GET("api/animals")
    suspend fun getMyAnimals(): List<AnimalResponse>

    @GET("api/animals/{id}")
    suspend fun getAnimal(
        @Path("id") id: Long,
    ): AnimalResponse

    @POST("api/animals")
    suspend fun addAnimal(
        @Body request: AnimalRequest,
    ): AnimalResponse

    @PUT("api/animals/{id}")
    suspend fun updateAnimal(
        @Path("id") id: Long,
        @Body request: AnimalRequest,
    ): AnimalResponse

    @DELETE("api/animals/{id}")
    suspend fun deleteAnimal(
        @Path("id") id: Long,
    ): Response<Unit>
}
