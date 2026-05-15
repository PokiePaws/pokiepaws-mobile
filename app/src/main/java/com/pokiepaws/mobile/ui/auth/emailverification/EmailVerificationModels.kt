package com.pokiepaws.mobile.ui.auth.emailverification

data class EmailVerificationUiState(
    val email: String,
)

sealed interface EmailVerificationEvent {
    data object BackToLoginClicked : EmailVerificationEvent
}

sealed interface EmailVerificationEffect {
    data object NavigateBackToLogin : EmailVerificationEffect
}
