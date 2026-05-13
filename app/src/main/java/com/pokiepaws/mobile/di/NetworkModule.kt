package com.pokiepaws.mobile.di

import com.pokiepaws.mobile.BuildConfig
import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.data.remote.service.AnimalApiService
import com.pokiepaws.mobile.data.remote.service.AuthApiService
import com.pokiepaws.mobile.data.remote.service.ClinicApiService
import com.pokiepaws.mobile.data.remote.service.VetApiService
import com.pokiepaws.mobile.data.remote.service.VisitApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val HTTP_UNAUTHORIZED = 401
    private const val TIMEOUT_SECONDS = 30L
    private val publicAuthPaths =
        setOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
        )

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { tokenManager.token.first() }
                val originalRequest = chain.request()
                val isPublicAuthRequest = originalRequest.url.encodedPath in publicAuthPaths
                val request = originalRequest.newBuilder()

                if (!token.isNullOrEmpty() && !isPublicAuthRequest) {
                    request.header("Authorization", "Bearer $token")
                }
                val response = chain.proceed(request.build())

                if (response.code == HTTP_UNAUTHORIZED && !isPublicAuthRequest) {
                    runBlocking { tokenManager.clearToken() }
                }

                response
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                },
            )
            .connectTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(normalizedBaseUrl())
            .client(client)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            )
            .build()
    }

    private fun normalizedBaseUrl() =
        BuildConfig.BASE_URL
            .trim()
            .trim('"')
            .let { if (it.endsWith("/")) it else "$it/" }
            .toHttpUrl()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAnimalApiService(retrofit: Retrofit): AnimalApiService {
        return retrofit.create(AnimalApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVisitApiService(retrofit: Retrofit): VisitApiService {
        return retrofit.create(VisitApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideClinicApiService(retrofit: Retrofit): ClinicApiService {
        return retrofit.create(ClinicApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVetApiService(retrofit: Retrofit): VetApiService {
        return retrofit.create(VetApiService::class.java)
    }
}
