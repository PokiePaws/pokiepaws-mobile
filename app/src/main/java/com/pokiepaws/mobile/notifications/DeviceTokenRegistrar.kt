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
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun registerCurrentToken() {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d("FCM_TOKEN", "Aktualny token: $token")
                    registerToken(token)
                }
                .addOnFailureListener { error ->
                    Log.e("FCM_TOKEN", "Nie udało się pobrać tokena FCM", error)
                }
        }

        fun registerToken(token: String) {
            scope.launch {
                try {
                    val response = authApiService.registerDeviceToken(DeviceTokenRequest(token = token))
                    if (response.isSuccessful) {
                        Log.d("FCM_TOKEN", "Token FCM zarejestrowany w backendzie")
                    } else {
                        throw HttpException(response)
                    }
                } catch (e: HttpException) {
                    Log.e("FCM_TOKEN", "Backend odrzucił token FCM: HTTP ${e.code()}", e)
                } catch (e: IOException) {
                    Log.e("FCM_TOKEN", "Błąd sieci przy rejestracji tokena FCM", e)
                }
            }
        }
    }
