package com.pokiepaws.mobile.ui.animals.animallist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pokiepaws.mobile.R

@Composable
fun AnimalListScreen(
    onAddAnimal: () -> Unit,
    onAnimalClick: (Long) -> Unit,
    addedAnimalName: String? = null,
    onAddedAnimalMessageShown: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AnimalListViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }
    val animalAddedMessage = stringResource(R.string.animal_added_success)

    LaunchedEffect(addedAnimalName) {
        val animalName = addedAnimalName ?: return@LaunchedEffect
        viewModel.loadAnimals()
        snackbarHostState.showSnackbar(String.format(animalAddedMessage, animalName))
        onAddedAnimalMessageShown()
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface),
        ) {
            AnimalListHeader(onAddAnimal)

            when (val state = uiState) {
                is AnimalListUiState.Loading -> {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                is AnimalListUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadAnimals() },
                    )
                }

                is AnimalListUiState.Success -> {
                    if (state.animals.isEmpty()) {
                        EmptyAnimalsView(onAddAnimal)
                    } else {
                        AnimalLazyList(
                            animals = state.animals,
                            onAnimalClick = onAnimalClick,
                        )
                    }
                }
            }
        }
    }
}
