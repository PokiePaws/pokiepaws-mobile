@file:Suppress("FunctionNaming")

package com.pokiepaws.mobile.ui.visits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.ui.theme.PokieBlueDark

private const val TIME_LABEL_LENGTH = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVisitScreen(
    animalId: Long,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateVisitViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadClinics() }

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    Scaffold(
        topBar = {
            CreateVisitTopBar(
                step = state.step,
                onBack = onBack,
                onPreviousStep = viewModel::goBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            CreateVisitContent(
                animalId = animalId,
                state = state,
                viewModel = viewModel,
            )

            ErrorSnackbar(
                error = state.error,
                onDismiss = viewModel::clearError,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateVisitTopBar(
    step: CreateVisitStep,
    onBack: () -> Unit,
    onPreviousStep: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = step.title) },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (step == CreateVisitStep.SELECT_CLINIC) {
                        onBack()
                    } else {
                        onPreviousStep()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Wroc",
                )
            }
        },
    )
}

@Composable
private fun CreateVisitContent(
    animalId: Long,
    state: CreateVisitUiState,
    viewModel: CreateVisitViewModel,
) {
    when (state.step) {
        CreateVisitStep.SELECT_ANIMAL,
        CreateVisitStep.SELECT_CLINIC,
        ->
            LoadingOrContent(isLoading = state.isLoading) {
                ClinicListStep(
                    clinics = state.clinics,
                    onSelect = viewModel::selectClinic,
                )
            }

        CreateVisitStep.SELECT_VET ->
            LoadingOrContent(isLoading = state.isLoading) {
                VetListStep(
                    vets = state.vets,
                    onSelect = viewModel::selectVet,
                )
            }

        CreateVisitStep.SELECT_SLOT ->
            SlotStep(
                selectedDate = state.selectedDate,
                slots = state.availableSlots,
                isLoading = state.isLoading,
                onDateSelected = viewModel::selectDate,
                onSlotSelected = viewModel::selectSlot,
            )

        CreateVisitStep.CONFIRM ->
            ConfirmStep(
                state = state,
                description = state.description,
                onDescriptionChange = viewModel::updateDescription,
                onConfirm = { viewModel.confirm(animalId) },
                isLoading = state.isLoading,
            )
    }
}

@Composable
private fun LoadingOrContent(
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        content()
    }
}

@Composable
private fun ErrorSnackbar(
    error: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    error?.let { err ->
        Snackbar(
            modifier = modifier.padding(16.dp),
            dismissAction = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
        ) {
            Text(err)
        }
    }
}

private val CreateVisitStep.title: String
    get() =
        when (this) {
            CreateVisitStep.SELECT_CLINIC -> "Wybierz gabinet"
            CreateVisitStep.SELECT_VET -> "Wybierz weterynarza"
            CreateVisitStep.SELECT_SLOT -> "Wybierz termin"
            CreateVisitStep.SELECT_ANIMAL -> "Wybierz zwierze"
            CreateVisitStep.CONFIRM -> "Potwierdz wizyte"
        }

@Composable
private fun ClinicListStep(
    clinics: List<Clinic>,
    onSelect: (Clinic) -> Unit,
) {
    if (clinics.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("đźŹĄ", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Brak dostÄ™pnych gabinetĂłw",
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(clinics, key = { it.id }) { clinic ->
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(clinic) },
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = clinic.clinicName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PokieBlueDark,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${clinic.street} ${clinic.houseNumber}, ${clinic.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    clinic.phone?.let {
                        Text(
                            text = "đź“ž $it",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VetListStep(
    vets: List<Vet>,
    onSelect: (Vet) -> Unit,
) {
    if (vets.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("đź‘¨â€Ťâš•ď¸Ź", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Brak weterynarzy w tym gabinecie",
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(vets, key = { it.userId }) { vet ->
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(vet) },
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("đź‘¨â€Ťâš•ď¸Ź", fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${vet.firstName} ${vet.lastName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PokieBlueDark,
                        )
                        vet.specialization?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotStep(
    selectedDate: String?,
    slots: List<String>,
    isLoading: Boolean,
    onDateSelected: (String) -> Unit,
    onSlotSelected: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        var dateInput by remember { mutableStateOf(selectedDate ?: "") }

        Text(
            text = "Wybierz datÄ™ wizyty",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PokieBlueDark,
        )

        OutlinedTextField(
            value = dateInput,
            onValueChange = { dateInput = it },
            label = { Text("Data (YYYY-MM-DD)") },
            placeholder = { Text("np. 2026-05-16") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Button(
            onClick = { onDateSelected(dateInput) },
            modifier = Modifier.fillMaxWidth(),
            enabled = dateInput.matches(Regex("\\d{4}-\\d{2}-\\d{2}")),
        ) {
            Text("SprawdĹş dostÄ™pne terminy")
        }

        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (slots.isEmpty() && selectedDate != null) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Brak wolnych terminĂłw w tym dniu",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (slots.isNotEmpty()) {
            Text(
                text = "DostÄ™pne godziny:",
                style = MaterialTheme.typography.titleSmall,
                color = PokieBlueDark,
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(slots) { slot ->
                    val time = slot.substringAfter("T").take(TIME_LABEL_LENGTH)
                    OutlinedCard(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { onSlotSelected(slot) },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = "đź• $time",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmStep(
    state: CreateVisitUiState,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Podsumowanie wizyty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PokieBlueDark,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SummaryRow(
                    label = "đźŹĄ Gabinet",
                    value = state.selectedClinic?.clinicName ?: "",
                )
                SummaryRow(
                    label = "đź“Ť Adres",
                    value =
                        state.selectedClinic?.let {
                            "${it.street} ${it.houseNumber}, ${it.city}"
                        } ?: "",
                )
                SummaryRow(
                    label = "đź‘¨â€Ťâš•ď¸Ź Weterynarz",
                    value =
                        state.selectedVet?.let {
                            "${it.firstName} ${it.lastName}"
                        } ?: "",
                )
                SummaryRow(
                    label = "đź“… Termin",
                    value =
                        state.selectedSlot
                            ?.replace("T", " ")
                            ?.take(16) ?: "",
                )
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Opis wizyty (opcjonalnie)") },
            placeholder = { Text("PowĂłd wizyty, objawy...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp).width(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text("UmĂłw wizytÄ™")
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
