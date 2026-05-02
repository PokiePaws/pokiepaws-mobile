package com.pokiepaws.mobile.data.remote.interceptors

import com.pokiepaws.mobile.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val tokenManager: TokenManager,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = runBlocking { tokenManager.token.first() }
            val request = chain.request().newBuilder()
            token?.let {
                request.addHeader("Authorization", "Bearer $it")
            }
            return chain.proceed(request.build())
        }
    }
