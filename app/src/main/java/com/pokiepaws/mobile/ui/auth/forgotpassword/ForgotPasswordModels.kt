package com.pokiepaws.mobile.ui.auth.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)

sealed interface ForgotPasswordEvent {
    data class EmailChanged(val value: String) : ForgotPasswordEvent

    data object Submit : ForgotPasswordEvent

    data object NavigateBack : ForgotPasswordEvent
}

sealed interface ForgotPasswordEffect {
    data class EmailSent(val email: String) : ForgotPasswordEffect

    data object NavigateBack : ForgotPasswordEffect
}
