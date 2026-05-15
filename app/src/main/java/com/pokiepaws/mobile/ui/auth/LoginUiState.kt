package com.pokiepaws.mobile.ui.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent

    data class PasswordChanged(val value: String) : LoginEvent

    data object TogglePasswordVisibility : LoginEvent

    data object SubmitLogin : LoginEvent

    data object ForgotPasswordClicked : LoginEvent

    data object RegisterClicked : LoginEvent
}

sealed interface LoginEffect {
    data class NavigateAfterLogin(val token: String, val role: String) : LoginEffect

    data class ShowError(val message: String) : LoginEffect
}
