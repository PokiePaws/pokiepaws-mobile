package com.pokiepaws.mobile.ui.animals.animallist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalSpecies
import com.pokiepaws.mobile.ui.clinics.clinicslist.ClinicSearchBar

@Composable
fun AnimalListScreen(
    onAddAnimal: () -> Unit,
    onAnimalClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    addedAnimalName: String? = null,
    onAddedAnimalMessageShown: () -> Unit = {},
    viewModel: AnimalListViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }
    val animalAddedMessage = stringResource(R.string.animal_added_success)
    var searchQuery by remember { mutableStateOf("") }

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
            ClinicSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderRes = R.string.animal_search_placeholder,
                modifier =
                    Modifier
                        .padding(horizontal = 24.dp)
                        .offset(y = SEARCH_BAR_OFFSET.dp),
            )

            when (uiState) {
                is AnimalListUiState.Loading -> {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                is AnimalListUiState.Error -> {
                    ErrorView(
                        message = uiState.message,
                        onRetry = { viewModel.loadAnimals() },
                    )
                }

                is AnimalListUiState.Success -> {
                    val filteredAnimals = filterAnimalsBySearchQuery(uiState.animals, searchQuery)
                    if (uiState.animals.isEmpty()) {
                        EmptyAnimalsView(onAddAnimal)
                    } else if (filteredAnimals.isEmpty()) {
                        EmptyAnimalsSearchView(searchQuery = searchQuery)
                    } else {
                        AnimalLazyList(
                            animals = AnimalItems(filteredAnimals),
                            onAnimalClick = onAnimalClick,
                        )
                    }
                }
            }
        }
    }
}

private fun filterAnimalsBySearchQuery(
    animals: List<Animal>,
    searchQuery: String,
): List<Animal> {
    val q = searchQuery.trim().lowercase()
    if (q.isEmpty()) return animals

    return animals.filter { animal ->
        animal.name.lowercase().contains(q) ||
            animal.species.searchTerms.any { it.contains(q) }
    }
}

private val AnimalSpecies.searchTerms: List<String>
    get() =
        when (this) {
            AnimalSpecies.DOG -> listOf("dog", "pies")
            AnimalSpecies.CAT -> listOf("cat", "kot")
            AnimalSpecies.FERRET -> listOf("ferret", "fretka")
            AnimalSpecies.RABBIT -> listOf("rabbit", "krolik", "królik")
            AnimalSpecies.SMALL_MAMMAL -> listOf("small mammal", "gryzon", "maly ssak", "mały ssak")
            AnimalSpecies.HORSE -> listOf("horse", "kon", "koń")
            AnimalSpecies.CATTLE -> listOf("cattle", "cow", "bydlo", "bydło")
            AnimalSpecies.PIG -> listOf("pig", "swine", "swinia", "świnia")
            AnimalSpecies.SHEEP -> listOf("sheep", "owca")
            AnimalSpecies.GOAT -> listOf("goat", "koza")
            AnimalSpecies.POULTRY -> listOf("poultry", "drob", "drób")
            AnimalSpecies.OTHER -> listOf("other", "inne")
        }
private const val SEARCH_BAR_OFFSET = -24