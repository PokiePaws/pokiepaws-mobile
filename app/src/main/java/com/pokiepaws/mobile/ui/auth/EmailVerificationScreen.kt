package com.pokiepaws.mobile.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmailVerificationScreen(
    email: String,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = EmailVerificationUiState(email = email)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            EmailVerificationTitle()

            Spacer(modifier = Modifier.height(32.dp))

            EmailVerificationCard(
                state = state,
                onEvent = { event ->
                    when (event) {
                        EmailVerificationEvent.BackToLoginClicked -> onBackToLogin()
                    }
                },
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
