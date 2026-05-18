package com.pokiepaws.mobile.ui.settings

import androidx.compose.runtime.Immutable
import com.pokiepaws.mobile.domain.validation.PasswordValidationError

@Immutable
data class PasswordValidationErrors(val items: List<PasswordValidationError>)
