package com.pokiepaws.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.pokiepaws.mobile.domain.repository.AuthRepository
import com.pokiepaws.mobile.ui.nav.AppNavigation
import com.pokiepaws.mobile.ui.theme.PokiePawsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokiePawsTheme {
                AppNavigation(authRepository = authRepository)
            }
        }
    }
}
