package com.pokiepaws.mobile.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onEmailSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val serverFormatMessage = stringResource(R.string.forgot_password_server_format_message)

    val isLoading = uiState is AuthUiState.Loading
    val errorMessage = (uiState as? AuthUiState.Error)?.message

    var state by remember {
        mutableStateOf(
            ForgotPasswordUiState(
                email = "",
                isLoading = isLoading,
                errorMessage = errorMessage,
            ),
        )
    }

    LaunchedEffect(isLoading, errorMessage) {
        state =
            state.copy(
                isLoading = isLoading,
                errorMessage = errorMessage,
            )
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.ForgotPasswordSuccess) {
            onEmailSent(state.email)
            viewModel.resetState()
        }
    }

    ForgotPasswordContent(
        state = state,
        serverFormatMessage = serverFormatMessage,
        onEvent = { event ->
            when (event) {
                is ForgotPasswordEvent.EmailChanged -> state = state.copy(email = event.value)
                ForgotPasswordEvent.Submit -> viewModel.forgotPassword(state.email)
                ForgotPasswordEvent.NavigateBack -> onNavigateBack()
            }
        },
        modifier = modifier,
    )
}
