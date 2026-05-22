package com.pokiepaws.mobile.ui.animals.animalcard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.domain.model.AnimalSpecies
import com.pokiepaws.mobile.domain.model.RabiesRequirement
import com.pokiepaws.mobile.ui.animals.addanimal.animalSpeciesLabel
import com.pokiepaws.mobile.util.theme.PokieBlue
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun AnimalDetailsContent(
    animal: Animal,
    foreignTravelPlanned: Boolean,
    onVisitsHistoryClick: () -> Unit,
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
            DataCard(label = stringResource(R.string.animal_species_label), value = animalSpeciesLabel(animal.species))
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
            RabiesProphylaxisCard(
                animal = animal,
                foreignTravelPlanned = foreignTravelPlanned,
            )
            Button(
                onClick = onVisitsHistoryClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = PokieBlue,
                        contentColor = PokieWhite,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.animal_visits_history_button),
                    fontWeight = FontWeight.Bold,
                )
            }
            DataCard(
                label = stringResource(R.string.animal_notes_label),
                value = animal.notes.takeUnless { it.isNullOrBlank() } ?: stringResource(R.string.animal_no_notes),
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
fun AnimalHeader(
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
fun RabiesProphylaxisCard(
    animal: Animal,
    foreignTravelPlanned: Boolean,
    modifier: Modifier = Modifier,
) {
    val requirement = rabiesRequirementFor(animal.species, foreignTravelPlanned)
    val status =
        when (requirement) {
            RabiesRequirement.REQUIRED_BY_LAW -> stringResource(R.string.animal_rabies_required_by_law)
            RabiesRequirement.RECOMMENDED_OPTIONAL -> stringResource(R.string.animal_rabies_recommended_optional)
        }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PokieBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = PokieBlue,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.animal_prophylaxis_title),
                        fontWeight = FontWeight.Bold,
                        color = PokieBlueDark,
                    )
                    Text(
                        text = stringResource(R.string.animal_rabies_vaccination_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            ProphylaxisRow(
                label = stringResource(R.string.animal_rabies_next_due_label),
                value = vaccinationDateLabel(animal.nextRabiesVaccinationDate),
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProphylaxisRow(
                label = stringResource(R.string.animal_rabies_status_label),
                value = status,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.animal_foreign_travel_planned),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text =
                        stringResource(
                            if (foreignTravelPlanned) {
                                R.string.yes_option
                            } else {
                                R.string.no_option
                            },
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun ProphylaxisRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun genderLabel(gender: String?): String =
    when (gender) {
        "MALE" -> stringResource(R.string.animal_gender_male)
        "FEMALE" -> stringResource(R.string.animal_gender_female)
        "HERMAPHRODITE" -> stringResource(R.string.animal_gender_hermaphrodite)
        else -> stringResource(R.string.no_data)
    }

@Composable
fun birthDateLabel(birthDate: String?): String {
    val noData = stringResource(R.string.no_data)
    return birthDate?.let {
        try {
            LocalDate.parse(it).format(DateFormatter)
        } catch (e: DateTimeParseException) {
            Log.w("AnimalScreen", "Cannot parse animal date field", e)
            noData
        }
    } ?: noData
}

@Composable
fun weightLabel(weight: Double?): String = weight?.let { "%.1f kg".format(it) } ?: stringResource(R.string.animal_weight_not_provided)

@Composable
fun vaccinationDateLabel(value: String?): String {
    val noData = stringResource(R.string.no_data)
    return value?.let {
        try {
            LocalDate.parse(it).format(DateFormatter)
        } catch (e: DateTimeParseException) {
            Log.w("AnimalScreen", "Cannot parse animal date field", e)
            noData
        }
    } ?: noData
}

private fun rabiesRequirementFor(
    species: AnimalSpecies,
    foreignTravelPlanned: Boolean,
): RabiesRequirement =
    when {
        species == AnimalSpecies.DOG -> RabiesRequirement.REQUIRED_BY_LAW
        foreignTravelPlanned && species in setOf(AnimalSpecies.CAT, AnimalSpecies.FERRET) ->
            RabiesRequirement.REQUIRED_BY_LAW
        else -> RabiesRequirement.RECOMMENDED_OPTIONAL
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
            period.years > 0 -> context.resources.getQuantityString(R.plurals.animal_age_years, period.years, period.years)
            period.months > 0 -> context.resources.getQuantityString(R.plurals.animal_age_months, period.months, period.months)
            else -> newborn
        }
    } catch (e: DateTimeParseException) {
        Log.w("AnimalScreen", "Cannot parse animal age", e)
        noData
    }
}

private val DateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
