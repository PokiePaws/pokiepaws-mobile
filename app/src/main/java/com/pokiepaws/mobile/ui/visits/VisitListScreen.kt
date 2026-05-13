package com.pokiepaws.mobile.ui.visits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.ui.animals.AnimalViewModel
import com.pokiepaws.mobile.ui.animals.AnimalUiState
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieWhite

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private const val ADD_BUTTON_SIZE = 48

@Composable
fun VisitListScreen(
    onVisitClick: (Long) -> Unit,
    onCreateVisit: (Long) -> Unit, // animalId
    modifier: Modifier = Modifier,
    viewModel: VisitViewModel = hiltViewModel(),
    animalViewModel: AnimalViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val animalState by animalViewModel.uiState.collectAsState()
    var showAnimalPicker by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) { viewModel.loadUpcoming() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        // Header wzorowany na AnimalListScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(
                        bottomStart = HEADER_ROUNDING.dp,
                        bottomEnd = HEADER_ROUNDING.dp,
                    ),
                )
                .padding(top = HEADER_TOP_PADDING.dp, bottom = HEADER_BOTTOM_PADDING.dp)
                .padding(horizontal = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Moje wizyty",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieWhite,
                )

                // FAB — taki sam jak "+" w AnimalListScreen
                IconButton(
                    onClick = { showAnimalPicker = true },
                    modifier = Modifier
                        .size(ADD_BUTTON_SIZE.dp)
                        .clip(CircleShape)
                        .background(PokieWhite.copy(alpha = 0.2f)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Umów wizytę",
                        tint = PokieWhite,
                    )
                }
            }
        }

        // Lista wizyt
        when (val s = state) {
            is VisitUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is VisitUiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Błąd: ${s.message}")
            }

            is VisitUiState.Success -> {
                val visits = s.visits.sortedBy { it.startsAt }

                if (visits.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📅",
                                fontSize = 64.sp,
                            )
                            Text(
                                text = "Brak nadchodzących wizyt",
                                fontWeight = FontWeight.Bold,
                                color = PokieBlueDark,
                            )
                            TextButton(
                                onClick = { showAnimalPicker = true },
                                modifier = Modifier.padding(top = 8.dp),
                            ) {
                                Text("Umów pierwszą wizytę")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = visits,
                            key = { it.id },
                        ) { v ->
                            VisitCard(
                                visit = v,
                                onClick = { onVisitClick(v.id) },
                                onCancel = { viewModel.cancelVisit(v.id) },
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog wyboru zwierzęcia przed umawianiem wizyty
    if (showAnimalPicker) {
        AnimalPickerDialog(
            animalState = animalState,
            onAnimalSelected = { animal ->
                showAnimalPicker = false
                onCreateVisit(animal.id)
            },
            onDismiss = { showAnimalPicker = false },
        )
    }
}

@Composable
private fun AnimalPickerDialog(
    animalState: AnimalUiState,
    onAnimalSelected: (Animal) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wybierz zwierzę") },
        text = {
            when (animalState) {
                is AnimalUiState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                is AnimalUiState.Error -> Text(
                    text = "Nie udało się załadować zwierząt",
                    color = MaterialTheme.colorScheme.error,
                )

                is AnimalUiState.Success -> {
                    if (animalState.animals.isEmpty()) {
                        Text("Nie masz żadnych zwierząt. Dodaj najpierw zwierzę.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            animalState.animals.forEach { animal ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = { onAnimalSelected(animal) },
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(text = "🐾", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = animal.name,
                                                fontWeight = FontWeight.Bold,
                                                color = PokieBlueDark,
                                            )
                                            Text(
                                                text = animal.species,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
    )
}

@Composable
private fun VisitCard(
    visit: Visit,
    onClick: () -> Unit,
    onCancel: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Wizyta #${visit.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📅 ${visit.startsAt.replace("T", " ").take(16)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Status: ${visit.status}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (visit.status == "SCHEDULED") {
                TextButton(
                    onClick = { onCancel(visit.id) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Anuluj wizytę")
                }
            }
        }
    }
}