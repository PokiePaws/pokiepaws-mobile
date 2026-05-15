package com.pokiepaws.mobile.ui.animals

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieWhite
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val DateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@Composable
private fun genderLabel(gender: String?): String =
    when (gender) {
        "MALE" -> stringResource(R.string.animal_gender_male)
        "FEMALE" -> stringResource(R.string.animal_gender_female)
        "HERMAPHRODITE" -> stringResource(R.string.animal_gender_hermaphrodite)
        else -> stringResource(R.string.no_data)
    }

@Composable
private fun birthDateLabel(birthDate: String?): String {
    val noData = stringResource(R.string.no_data)
    return birthDate?.let {
        try {
            LocalDate.parse(it).format(DateFormatter)
        } catch (e: DateTimeParseException) {
            Log.w("AnimalScreen", "Cannot parse birth date: $it", e)
            noData
        }
    } ?: noData
}

@Composable
private fun weightLabel(weight: Double?): String =
    weight?.let { "%.1f kg".format(it) } ?: stringResource(R.string.animal_weight_not_provided)

@Composable
fun AnimalScreen(
    animalId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is AnimalUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is AnimalUiState.Success -> {
                val animal = state.animals.find { it.id == animalId }
                if (animal != null) {
                    AnimalDetailsContent(animal = animal, onBack = onBack)
                } else {
                    Text(
                        text = stringResource(R.string.animal_not_found, animalId),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }

            is AnimalUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                )
            }

            else -> {}
        }
    }
}

@Composable
fun AnimalDetailsContent(
    animal: Animal,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(PokieWhite)
                .verticalScroll(rememberScrollState()),
    ) {
        AnimalHeader(name = animal.name, onBack = onBack)

        Column(
            modifier =
                Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DataCard(label = stringResource(R.string.animal_species_label), value = animal.species)
            DataCard(
                label = stringResource(R.string.animal_breed_label),
                value = animal.breed ?: stringResource(R.string.animal_unknown_breed),
            )
            DataCard(label = stringResource(R.string.animal_gender_label), value = genderLabel(animal.gender))
            DataCard(
                label = stringResource(R.string.animal_color_label),
                value = animal.color ?: stringResource(R.string.no_data),
            )
            DataCard(
                label = stringResource(R.string.animal_age_label),
                value = animal.birthDate?.let { calculateAgeLabel(it) } ?: stringResource(R.string.no_data),
            )
            DataCard(label = stringResource(R.string.animal_birth_date_label), value = birthDateLabel(animal.birthDate))
            DataCard(label = stringResource(R.string.animal_weight_label), value = weightLabel(animal.weight))
            DataCard(
                label = stringResource(R.string.animal_microchip_label),
                value = animal.microchipNumber ?: stringResource(R.string.none_value),
            )
            DataCard(
                label = stringResource(R.string.animal_notes_label),
                value = animal.notes.takeUnless { it.isNullOrBlank() } ?: stringResource(R.string.animal_no_notes),
            )
        }
    }
}

@Composable
private fun AnimalHeader(
    name: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = PokieBlue,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                )
                .padding(top = 48.dp, bottom = 32.dp)
                .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(
            onClick = onBack,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PokieWhite.copy(alpha = 0.3f)),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_content_description),
                tint = PokieWhite,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🐾", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = name,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = PokieWhite,
            )
        }
    }
}

@Composable
fun DataCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun calculateAgeLabel(birthDate: String): String {
    val context = LocalContext.current
    val noData = stringResource(R.string.no_data)
    val newborn = stringResource(R.string.animal_age_newborn)

    return try {
        val date = LocalDate.parse(birthDate)
        val now = LocalDate.now()
        val period = Period.between(date, now)

        when {
            period.years > 0 -> context.getString(R.string.animal_age_years, period.years)
            period.months > 0 -> context.getString(R.string.animal_age_months, period.months)
            else -> newborn
        }
    } catch (e: DateTimeParseException) {
        Log.w("AnimalScreen", "Cannot parse age from: $birthDate", e)
        noData
    }
}
