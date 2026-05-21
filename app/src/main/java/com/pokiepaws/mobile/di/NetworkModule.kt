package com.pokiepaws.mobile.di

import com.pokiepaws.mobile.BuildConfig
import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.data.remote.dto.auth.RefreshTokenRequest
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
import okhttp3.Authenticator
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val HTTP_UNAUTHORIZED = 401
    private const val TIMEOUT_SECONDS = 30L
    private const val MAX_AUTH_RETRIES = 2

    private val publicAuthPaths =
        setOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/auth/refresh",
            "/api/auth/logout",
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
    fun provideOkHttpClient(
        tokenManager: TokenManager,
        json: Json,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { tokenManager.token.first() }
                val originalRequest = chain.request()
                val isPublicAuthRequest = originalRequest.url.encodedPath in publicAuthPaths

                val requestBuilder = originalRequest.newBuilder()
                if (!token.isNullOrEmpty() && !isPublicAuthRequest) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .authenticator(refreshTokenAuthenticator(tokenManager, json))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    redactHeader("Authorization")
                    redactHeader("Cookie")
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BASIC
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            )
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    private fun refreshTokenAuthenticator(
        tokenManager: TokenManager,
        json: Json,
    ): Authenticator =
        object : Authenticator {
            override fun authenticate(
                route: Route?,
                response: Response,
            ): okhttp3.Request? {
                val shouldTryRefresh =
                    response.code == HTTP_UNAUTHORIZED && response.responseCount < MAX_AUTH_RETRIES

                val resultRequest: okhttp3.Request? =
                    if (!shouldTryRefresh) {
                        null
                    } else {
                        val refreshToken = runBlocking { tokenManager.refreshToken.first() }

                        if (refreshToken.isNullOrBlank()) {
                            null
                        } else {
                            val refreshedAccessToken: String? =
                                synchronized(this) {
                                    runBlocking {
                                        runCatching {
                                            val authApi =
                                                Retrofit.Builder()
                                                    .baseUrl(normalizedBaseUrl())
                                                    .client(OkHttpClient.Builder().build())
                                                    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                                                    .build()
                                                    .create(AuthApiService::class.java)

                                            val authResponse = authApi.refresh(RefreshTokenRequest(refreshToken))
                                            val accessToken = requireNotNull(authResponse.resolvedToken)

                                            tokenManager.saveTokens(
                                                accessToken = accessToken,
                                                refreshToken = authResponse.resolvedRefreshToken,
                                            )
                                            accessToken
                                        }.getOrElse {
                                            tokenManager.clearToken()
                                            null
                                        }
                                    }
                                }

                            if (refreshedAccessToken == null) {
                                null
                            } else {
                                response.request
                                    .newBuilder()
                                    .header("Authorization", "Bearer $refreshedAccessToken")
                                    .build()
                            }
                        }
                    }

                return resultRequest
            }
        }

    private val Response.responseCount: Int
        get() {
            var current: Response? = this
            var count = 1
            while (current?.priorResponse != null) {
                count++
                current = current.priorResponse
            }
            return count
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
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
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
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideAnimalApiService(retrofit: Retrofit): AnimalApiService = retrofit.create(AnimalApiService::class.java)

    @Provides
    @Singleton
    fun provideVisitApiService(retrofit: Retrofit): VisitApiService = retrofit.create(VisitApiService::class.java)

    @Provides
    @Singleton
    fun provideClinicApiService(retrofit: Retrofit): ClinicApiService = retrofit.create(ClinicApiService::class.java)

    @Provides
    @Singleton
    fun provideVetApiService(retrofit: Retrofit): VetApiService = retrofit.create(VetApiService::class.java)
}
