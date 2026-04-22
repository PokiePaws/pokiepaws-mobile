package com.pokiepaws.mobile.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AnimalApiService {
    @GET("animals")
    suspend fun getMyAnimals(): List<AnimalResponse>

    @GET("animals/{id}")
    suspend fun getAnimal(
        @Path("id") id: Long,
    ): AnimalResponse

    @POST("animals")
    suspend fun addAnimal(
        @Body request: AnimalRequest,
    ): AnimalResponse

    @PUT("animals/{id}")
    suspend fun updateAnimal(
        @Path("id") id: Long,
        @Body request: AnimalRequest,
    ): AnimalResponse

    @DELETE("animals/{id}")
    suspend fun deleteAnimal(
        @Path("id") id: Long,
    ): Response<Unit>
}
