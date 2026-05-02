package com.pokiepaws.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.ui.nav.AppNavigation
import com.pokiepaws.mobile.ui.theme.PokiePawsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokiePawsTheme {
                AppNavigation(tokenManager = tokenManager)
            }
        }
    }
}
