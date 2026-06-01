package com.pokiepaws.mobile.ui.animals.addanimal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnimalScreen(
    onBack: () -> Unit,
    onAnimalAdded: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddAnimalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    AddAnimalContent(
        uiState = uiState,
        isOnline = isOnline,
        onBack = onBack,
        onAnimalAdded = onAnimalAdded,
        modifier = modifier,
        onAddAnimal = viewModel::addAnimal,
    )
}
