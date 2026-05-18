package com.pokiepaws.mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.pokiepaws.mobile.domain.repository.AuthRepository
import com.pokiepaws.mobile.navigation.AppNavigation
import com.pokiepaws.mobile.notifications.DeviceTokenRegistrar
import com.pokiepaws.mobile.util.theme.PokiePawsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var deviceTokenRegistrar: DeviceTokenRegistrar

    private var lastTokenRegisteredForSession: String? = null

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Permission result only controls system notification display on Android 13+.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        registerDeviceTokenWhenAuthenticated()
        setContent {
            PokiePawsTheme {
                AppNavigation(authRepository = authRepository)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permissionGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun registerDeviceTokenWhenAuthenticated() {
        lifecycleScope.launch {
            authRepository.token.collect { token ->
                if (!token.isNullOrBlank() && token != lastTokenRegisteredForSession) {
                    lastTokenRegisteredForSession = token
                    deviceTokenRegistrar.registerCurrentToken()
                }
            }
        }
    }
}
