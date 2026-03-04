package com.pokiepaws.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pokiepaws.mobile.ui.nav.AppNavigation
import com.pokiepaws.mobile.ui.theme.PokiePawsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokiePawsTheme {
                AppNavigation()
            }
        }
    }
}