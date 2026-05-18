package com.pokiepaws.mobile.ui.visits.visitlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.ui.animals.addanimal.animalSpeciesLabel
import com.pokiepaws.mobile.ui.animals.animallist.AnimalListUiState
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private const val ADD_BUTTON_SIZE = 48

@Composable
fun VisitListContent(
    state: VisitListUiState,
    animalState: AnimalListUiState,
    onVisitClick: (Long) -> Unit,
    onCreateVisit: (Long) -> Unit,
    onCancelVisit: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAnimalPicker by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape =
                            RoundedCornerShape(
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
                IconButton(
                    onClick = { showAnimalPicker = true },
                    modifier =
                        Modifier
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
        when (val s = state) {
            is VisitListUiState.Loading ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

            is VisitListUiState.Error ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Błąd: ${s.message}")
                }

            is VisitListUiState.Success -> {
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
                                onCancel = onCancelVisit,
                            )
                        }
                    }
                }
            }
        }
    }
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
    animalState: AnimalListUiState,
    onAnimalSelected: (Animal) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wybierz zwierzę") },
        text = {
            when (animalState) {
                is AnimalListUiState.Loading ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }

                is AnimalListUiState.Error ->
                    Text(
                        text = "Nie udało się załadować zwierząt",
                        color = MaterialTheme.colorScheme.error,
                    )

                is AnimalListUiState.Success -> {
                    if (animalState.animals.isEmpty()) {
                        Text("Nie masz żadnych zwierząt. Dodaj najpierw zwierzę.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            animalState.animals.forEach { animal ->
                                Card(
                                    modifier =
                                        Modifier
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
                                                text = animalSpeciesLabel(animal.species),
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PokieWhite),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (visit.status == "SCHEDULED") "📅" else "✅",
                    fontSize = 36.sp,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Wizyta #${visit.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
                Text(
                    text = visit.startsAt.replace("T", " ").take(16),
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
                Text(
                    text = visit.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp),
                    color =
                        if (visit.status == "SCHEDULED") {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
                if (visit.status == "SCHEDULED") {
                    TextButton(
                        onClick = { onCancel(visit.id) },
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        colors =
                            ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                    ) {
                        Text("Anuluj wizytę", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
