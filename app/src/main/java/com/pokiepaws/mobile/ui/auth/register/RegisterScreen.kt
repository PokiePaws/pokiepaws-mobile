package com.pokiepaws.mobile.ui.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    onNavigateToVerification: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is RegisterEffect.NavigateToVerification -> onNavigateToVerification(effect.email)
                RegisterEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    RegisterContent(
        state = state,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
