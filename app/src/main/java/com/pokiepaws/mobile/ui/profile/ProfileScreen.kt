package com.pokiepaws.mobile.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    onLanguageClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    ProfileContent(
        state = state,
        onLogout = onLogout,
        onSettingsClick = onSettingsClick,
        onLanguageClick = onLanguageClick,
        onRetry = viewModel::loadProfile,
        modifier = modifier,
    )
}
