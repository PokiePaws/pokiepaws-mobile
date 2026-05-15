package com.pokiepaws.mobile.ui.auth.forgotpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onEmailSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val serverFormatMessage = stringResource(R.string.forgot_password_server_format_message)

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is ForgotPasswordEffect.EmailSent -> onEmailSent(effect.email)
                ForgotPasswordEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    ForgotPasswordContent(
        state = state,
        serverFormatMessage = serverFormatMessage,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
