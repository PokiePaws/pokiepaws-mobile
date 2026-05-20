package com.pokiepaws.mobile.ui.profile

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val initials: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

data class ProfileMenuItem(
    val icon: ImageVector,
    @StringRes val labelRes: Int,
    val iconColor: Color,
    val bgColor: Color,
    val onClick: () -> Unit = {},
)
