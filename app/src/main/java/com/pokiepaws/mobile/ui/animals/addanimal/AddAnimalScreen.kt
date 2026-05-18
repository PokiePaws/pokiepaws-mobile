package com.pokiepaws.mobile.ui.animals.addanimal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
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
    AddAnimalContent(
        onBack = onBack,
        onAnimalAdded = onAnimalAdded,
        modifier = modifier,
        onAddAnimal = viewModel::addAnimal,
    )
}
