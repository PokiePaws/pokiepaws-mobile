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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitType
import com.pokiepaws.mobile.ui.animals.addanimal.animalSpeciesLabel
import com.pokiepaws.mobile.ui.animals.animallist.AnimalListUiState
import com.pokiepaws.mobile.ui.clinics.clinicslist.ClinicSearchBar
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private const val ADD_BUTTON_SIZE = 48
private const val SEARCH_BAR_OFFSET = -24
private val VisitDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

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
    var searchQuery by remember { mutableStateOf("") }

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
                    .padding(top = HEADER_TOP_PADDING.dp, bottom = (HEADER_BOTTOM_PADDING + 24).dp)
                    .padding(horizontal = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.visits_my_visits),
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
                        contentDescription = stringResource(R.string.visit_schedule_content_description),
                        tint = PokieWhite,
                    )
                }
            }
        }
        ClinicSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderRes = R.string.visit_search_placeholder,
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .offset(y = SEARCH_BAR_OFFSET.dp),
        )
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
                    Text(text = stringResource(R.string.visit_error, s.message))
                }

            is VisitListUiState.Success -> {
                val visits = s.visits.sortedBy { it.startsAt }
                val animalNames = animalState.animalNamesById()
                val filteredVisits =
                    remember(visits, animalNames, searchQuery) {
                        visits.filterBySearchQuery(
                            searchQuery = searchQuery,
                            animalNames = animalNames,
                        )
                    }

                if (visits.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            VisitStateIcon(icon = Icons.Default.CalendarMonth)
                            Text(
                                text = stringResource(R.string.visits_empty),
                                fontWeight = FontWeight.Bold,
                                color = PokieBlueDark,
                            )
                            TextButton(
                                onClick = { showAnimalPicker = true },
                                modifier = Modifier.padding(top = 8.dp),
                            ) {
                                Text(stringResource(R.string.visit_book_first))
                            }
                        }
                    }
                } else if (filteredVisits.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            VisitStateIcon(icon = Icons.Default.Search)
                            Text(
                                text = stringResource(R.string.visits_no_results, searchQuery),
                                fontWeight = FontWeight.Bold,
                                color = PokieBlueDark,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = filteredVisits,
                            key = { it.id },
                        ) { v ->
                            VisitCard(
                                visit = v,
                                animalName = animalNames[v.animalId],
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
        title = { Text(stringResource(R.string.visit_select_animal_title)) },
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
                        text = stringResource(R.string.visit_animals_load_error),
                        color = MaterialTheme.colorScheme.error,
                    )

                is AnimalListUiState.Success -> {
                    if (animalState.animals.isEmpty()) {
                        Text(stringResource(R.string.visit_no_animals_dialog))
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
                                        Icon(
                                            imageVector = Icons.Default.Pets,
                                            contentDescription = null,
                                            tint = PokieBlueDark,
                                            modifier = Modifier.size(24.dp),
                                        )
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
                Text(stringResource(R.string.cancel_button))
            }
        },
    )
}

@Composable
private fun VisitCard(
    visit: Visit,
    animalName: String?,
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
                        .background(PokieBlueDark.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                if (visit.status == "CANCELLED") {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = stringResource(R.string.visit_cancelled_content_description),
                        tint = PokieBlueDark,
                        modifier = Modifier.size(36.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = stringResource(R.string.visit_scheduled_content_description),
                        tint = PokieBlueDark,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(visit.type.titleRes),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
                Text(
                    text =
                        stringResource(
                            R.string.visit_card_animal_title,
                            animalName ?: stringResource(R.string.animal_unknown_name),
                            visit.id,
                        ),
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
                Text(
                    text = stringResource(R.string.visit_card_date, visit.startsAt.toVisitDateLabel()),
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
                Text(
                    text = stringResource(R.string.visit_card_status, visit.status),
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
                    Button(
                        onClick = { onCancel(visit.id) },
                        modifier = Modifier.padding(top = 6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = PokieWhite,
                            ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(stringResource(R.string.visit_cancel_button), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun VisitStateIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PokieBlueDark.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PokieBlueDark,
            modifier = Modifier.size(36.dp),
        )
    }
}

private fun AnimalListUiState.animalNamesById(): Map<Long, String> =
    when (this) {
        is AnimalListUiState.Success -> animals.associate { it.id to it.name }
        else -> emptyMap()
    }

private fun List<Visit>.filterBySearchQuery(
    searchQuery: String,
    animalNames: Map<Long, String>,
): List<Visit> {
    val q = searchQuery.trim().lowercase()
    if (q.isEmpty()) return this

    return filter { visit ->
        val animalName = animalNames[visit.animalId].orEmpty()
        val dateLabel = visit.startsAt.toVisitDateLabel()
        animalName.lowercase().contains(q) ||
            visit.status.lowercase().contains(q) ||
            dateLabel.lowercase().contains(q) ||
            visit.startsAt.lowercase().contains(q)
    }
}

private fun String.toVisitDateLabel(): String =
    runCatching { LocalDateTime.parse(this).format(VisitDateFormatter) }
        .recoverCatching { substringBefore("T").split("-").let { "${it[2]}-${it[1]}-${it[0]}" } }
        .getOrDefault(this)

private val VisitType.titleRes: Int
    get() =
        when (this) {
            VisitType.CHECKUP -> R.string.visit_type_checkup
            VisitType.VACCINATION -> R.string.visit_type_vaccination
            VisitType.EMERGENCY -> R.string.visit_type_emergency
            VisitType.PREVENTIVE_CARE -> R.string.visit_type_prevention
            VisitType.SPECIALIST_CONSULTATION -> R.string.visit_type_specialist_consultation
            VisitType.DIAGNOSTIC_EXAM -> R.string.visit_type_diagnostic_exam
            VisitType.SURGICAL_PROCEDURE -> R.string.visit_type_surgery
            VisitType.DENTAL_PROCEDURE -> R.string.visit_type_dental_procedure
        }
