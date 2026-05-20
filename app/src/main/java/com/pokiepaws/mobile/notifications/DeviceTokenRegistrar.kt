package com.pokiepaws.mobile.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.pokiepaws.mobile.data.remote.dto.auth.DeviceTokenRequest
import com.pokiepaws.mobile.data.remote.service.AuthApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceTokenRegistrar
    @Inject
    constructor(
        private val authApiService: AuthApiService,
    ) {
        private companion object {
            const val LOG_TAG = "FCM_REGISTRATION"
        }

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun registerCurrentToken() {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d(LOG_TAG, "Current FCM registration value fetched")
                    registerToken(token)
                }
                .addOnFailureListener { error ->
                    Log.e(LOG_TAG, "Failed to fetch FCM registration value", error)
                }
        }

        fun registerToken(token: String) {
            scope.launch {
                try {
                    val response = authApiService.registerDeviceToken(DeviceTokenRequest(token = token))
                    if (response.isSuccessful) {
                        Log.d(LOG_TAG, "FCM registration value sent to backend")
                    } else {
                        throw HttpException(response)
                    }
                } catch (e: HttpException) {
                    Log.e(LOG_TAG, "Backend rejected FCM registration: HTTP ${e.code()}", e)
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "Network error while registering FCM", e)
                }
            }
        }
    }
