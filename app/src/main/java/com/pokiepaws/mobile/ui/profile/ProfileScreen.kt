package com.pokiepaws.mobile.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    onLanguageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileContent(
        onLogout = onLogout,
        onSettingsClick = onSettingsClick,
        onLanguageClick = onLanguageClick,
        modifier = modifier,
    )
}
