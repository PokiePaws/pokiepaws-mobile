package com.pokiepaws.mobile.ui.animals.addanimal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.AnimalDraft
import com.pokiepaws.mobile.domain.model.AnimalSpecies
import com.pokiepaws.mobile.domain.model.AnimalSpeciesCategory
import com.pokiepaws.mobile.util.theme.PokieBlue
import com.pokiepaws.mobile.util.theme.PokieWhite
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnimalContent(
    uiState: AddAnimalUiState,
    isOnline: Boolean,
    onBack: () -> Unit,
    onAnimalAdded: (String) -> Unit,
    modifier: Modifier = Modifier,
    onAddAnimal: (AnimalDraft, () -> Unit) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf(AnimalSpecies.DOG) }
    var breed by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("MALE") }
    var color by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var microchipNumber by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSpeciesPicker by remember { mutableStateOf(false) }
    var speciesCategory by remember { mutableStateOf(species.category) }

    val selectableDates =
        remember {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= System.currentTimeMillis()

                override fun isSelectableYear(year: Int): Boolean = year <= LocalDate.now().year
            }
        }
    val datePickerState = rememberDatePickerState(selectableDates = selectableDates)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val scrollState = rememberScrollState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date =
                                Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            birthDate = date.format(formatter)
                        }
                        showDatePicker = false
                    },
                ) {
                    Text(stringResource(R.string.ok_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showSpeciesPicker) {
        SpeciesPickerDialog(
            selectedSpecies = species,
            selectedCategory = speciesCategory,
            onCategorySelected = { speciesCategory = it },
            onSpeciesSelected = {
                species = it
                speciesCategory = it.category
                showSpeciesPicker = false
            },
            onDismiss = { showSpeciesPicker = false },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.add_animal_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_description),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(30.dp),
                color = PokieBlue.copy(alpha = 0.1f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = PokieBlue,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.animal_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )
            SpeciesSelector(
                selectedSpecies = species,
                onClick = {
                    speciesCategory = species.category
                    showSpeciesPicker = true
                },
            )
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text(stringResource(R.string.animal_breed_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )
            GenderSelector(
                selectedGender = gender,
                onGenderSelected = { gender = it },
            )
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text(stringResource(R.string.animal_color_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text(stringResource(R.string.animal_weight_kg_label)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                )
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { },
                        label = { Text(stringResource(R.string.animal_birth_short_label)) },
                        placeholder = { Text(stringResource(R.string.date_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        enabled = false,
                    )
                    Box(
                        modifier =
                            Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true },
                    )
                }
            }
            OutlinedTextField(
                value = microchipNumber,
                onValueChange = { microchipNumber = it },
                label = { Text(stringResource(R.string.animal_chip_number_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.animal_notes_input_label)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                shape = RoundedCornerShape(12.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (!isOnline) {
                Snackbar(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.online_action_required))
                }
            } else if (uiState is AddAnimalUiState.Error) {
                Snackbar(modifier = Modifier.fillMaxWidth()) {
                    Text(uiState.message)
                }
            }
            Button(
                onClick = {
                    val animalName = name.trim()
                    val animal =
                        AnimalDraft(
                            name = animalName,
                            species = species,
                            breed = breed.ifBlank { null },
                            gender = gender,
                            color = color.ifBlank { null },
                            birthDate = birthDate.ifBlank { null },
                            microchipNumber = microchipNumber.ifBlank { null },
                            weight = weight.toDoubleOrNull(),
                            notes = notes.ifBlank { null },
                        )
                    onAddAnimal(animal) {
                        onAnimalAdded(animalName)
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PokieBlue),
                enabled = name.isNotBlank() && isOnline && uiState !is AddAnimalUiState.Loading,
            ) {
                Text(
                    text = stringResource(R.string.animal_save_button),
                    fontSize = 16.sp,
                    color = PokieWhite,
                )
            }
        }
    }
}

@Composable
private fun GenderSelector(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val genderOptions =
        listOf(
            "MALE" to R.string.animal_gender_male,
            "FEMALE" to R.string.animal_gender_female,
            "HERMAPHRODITE" to R.string.animal_gender_hermaphrodite,
        )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.animal_gender_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            genderOptions.forEach { (value, labelRes) ->
                FilterChip(
                    selected = selectedGender == value,
                    onClick = { onGenderSelected(value) },
                    label = { Text(stringResource(labelRes)) },
                )
            }
        }
    }
}

@Composable
fun SpeciesSelector(
    selectedSpecies: AnimalSpecies,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = animalSpeciesLabel(selectedSpecies),
            onValueChange = { },
            label = { Text(stringResource(R.string.animal_species_label)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            readOnly = true,
            enabled = false,
        )
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .clickable(onClick = onClick),
        )
    }
}

@Composable
fun SpeciesPickerDialog(
    selectedSpecies: AnimalSpecies,
    selectedCategory: AnimalSpeciesCategory,
    onCategorySelected: (AnimalSpeciesCategory) -> Unit,
    onSpeciesSelected: (AnimalSpecies) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.animal_species_picker_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnimalSpeciesCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(animalSpeciesCategoryLabel(category)) },
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnimalSpecies.entries
                        .filter { it.category == selectedCategory }
                        .forEach { species ->
                            FilterChip(
                                selected = selectedSpecies == species,
                                onClick = { onSpeciesSelected(species) },
                                label = { Text(animalSpeciesLabel(species)) },
                            )
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
fun animalSpeciesLabel(species: AnimalSpecies): String =
    when (species) {
        AnimalSpecies.DOG -> stringResource(R.string.animal_species_dog)
        AnimalSpecies.CAT -> stringResource(R.string.animal_species_cat)
        AnimalSpecies.FERRET -> stringResource(R.string.animal_species_ferret)
        AnimalSpecies.RABBIT -> stringResource(R.string.animal_species_rabbit)
        AnimalSpecies.SMALL_MAMMAL -> stringResource(R.string.animal_species_small_mammal)
        AnimalSpecies.HORSE -> stringResource(R.string.animal_species_horse)
        AnimalSpecies.CATTLE -> stringResource(R.string.animal_species_cattle)
        AnimalSpecies.PIG -> stringResource(R.string.animal_species_pig)
        AnimalSpecies.SHEEP -> stringResource(R.string.animal_species_sheep)
        AnimalSpecies.GOAT -> stringResource(R.string.animal_species_goat)
        AnimalSpecies.POULTRY -> stringResource(R.string.animal_species_poultry)
        AnimalSpecies.OTHER -> stringResource(R.string.animal_species_other)
    }

@Composable
fun animalSpeciesCategoryLabel(category: AnimalSpeciesCategory): String =
    when (category) {
        AnimalSpeciesCategory.DOMESTIC -> stringResource(R.string.animal_species_category_domestic)
        AnimalSpeciesCategory.FARM -> stringResource(R.string.animal_species_category_farm)
    }
