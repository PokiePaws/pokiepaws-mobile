package com.pokiepaws.mobile.ui.animals.animalvisitshistory

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.util.theme.PokieBlue
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun AnimalVisitsHistoryContent(
    state: AnimalVisitsHistoryUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        HistoryHeader(onBack = onBack)

        when (state) {
            AnimalVisitsHistoryUiState.Loading ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PokieBlue)
                }

            is AnimalVisitsHistoryUiState.Error ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )
                }

            is AnimalVisitsHistoryUiState.Success ->
                VisitsHistoryList(visits = state.visits)
        }
    }
}

@Composable
private fun HistoryHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = PokieBlue,
                    shape = RoundedCornerShape(bottomStart = HEADER_ROUNDING.dp, bottomEnd = HEADER_ROUNDING.dp),
                )
                .padding(top = HEADER_TOP_PADDING.dp, bottom = HEADER_BOTTOM_PADDING.dp)
                .padding(horizontal = 24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier =
                    Modifier
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
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.animal_visits_history_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PokieWhite,
            )
        }
    }
}

@Composable
private fun VisitsHistoryList(
    @SuppressLint("ComposeUnstableCollections") visits: List<Visit>,
) {
    var fromDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var toDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    val fromDate = remember(fromDateMillis) { fromDateMillis?.toLocalDate() }
    val toDate = remember(toDateMillis) { toDateMillis?.toLocalDate() }
    val sortedVisits =
        remember(visits, fromDate, toDate) {
            visits
                .filterByDateRange(fromDate = fromDate, toDate = toDate)
                .sortedByDescending { it.startsAt }
        }

    Column(modifier = Modifier.fillMaxSize()) {
        DateRangePickerRow(
            fromDateMillis = fromDateMillis,
            toDateMillis = toDateMillis,
            onFromDateSelected = { fromDateMillis = it },
            onToDateSelected = { toDateMillis = it },
            onClearDates = {
                fromDateMillis = null
                toDateMillis = null
            },
        )

        if (sortedVisits.isEmpty()) {
            EmptyHistoryState(
                message =
                    if (visits.isEmpty()) {
                        stringResource(R.string.animal_visits_history_empty)
                    } else {
                        stringResource(R.string.animal_visits_history_empty_range)
                    },
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = sortedVisits,
                key = { it.id },
            ) { visit ->
                VisitHistoryCard(visit = visit)
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            VisitIcon(icon = Icons.Default.CalendarMonth)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
        }
    }
}

@Composable
private fun DateRangePickerRow(
    fromDateMillis: Long?,
    toDateMillis: Long?,
    onFromDateSelected: (Long?) -> Unit,
    onToDateSelected: (Long?) -> Unit,
    onClearDates: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DateFilterButton(
                label = stringResource(R.string.animal_visits_history_from),
                selectedDateMillis = fromDateMillis,
                onDateSelected = onFromDateSelected,
                modifier = Modifier.weight(1f),
            )
            DateFilterButton(
                label = stringResource(R.string.animal_visits_history_to),
                selectedDateMillis = toDateMillis,
                onDateSelected = onToDateSelected,
                modifier = Modifier.weight(1f),
            )
        }
        if (fromDateMillis != null || toDateMillis != null) {
            TextButton(
                onClick = onClearDates,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(R.string.animal_visits_history_clear_dates))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateFilterButton(
    label: String,
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                    },
                ) {
                    Text(stringResource(R.string.ok_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    OutlinedButton(
        onClick = { },
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = PokieBlueDark,
            ),
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = selectedDateMillis?.toDateLabel() ?: label,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun VisitHistoryCard(
    visit: Visit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VisitIcon(icon = Icons.Default.CalendarMonth, size = 48)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(visit.description.titleRes),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PokieBlueDark,
                    )
                    Text(
                        text = visit.startsAt.toVisitDateLabel(),
                        fontSize = 13.sp,
                        color = Color.Gray,
                    )
                }
                Text(
                    text = visit.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            HistoryInfoRow(
                icon = Icons.Default.MedicalServices,
                label = stringResource(R.string.animal_visits_history_start),
                value = visit.startsAt.toVisitDateLabel(),
            )
            HistoryInfoRow(
                icon = Icons.Default.CalendarMonth,
                label = stringResource(R.string.animal_visits_history_end),
                value = visit.endsAt.toVisitDateLabel(),
            )
            visit.disease?.takeIf { it.isNotBlank() }?.let {
                HistoryInfoRow(
                    icon = Icons.Default.Healing,
                    label = stringResource(R.string.visit_disease_label),
                    value = it,
                )
            }
            visit.diagnosis?.takeIf { it.isNotBlank() }?.let {
                HistoryInfoRow(
                    icon = Icons.Default.Science,
                    label = stringResource(R.string.visit_diagnosis_label),
                    value = it,
                )
            }
            HistoryInfoRow(
                icon = Icons.Default.Recommend,
                label = stringResource(R.string.animal_visits_history_prescriptions),
                value =
                    visit.recommendations?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.animal_visits_history_no_prescriptions),
            )
        }
    }
}

@Composable
private fun HistoryInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PokieBlueDark,
            modifier =
                Modifier
                    .padding(top = 2.dp)
                    .size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PokieBlueDark,
            )
            Text(
                text = value,
                fontSize = 13.sp,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun VisitIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Int = 64,
) {
    Box(
        modifier =
            modifier
                .size(size.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PokieBlueDark.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PokieBlueDark,
            modifier = Modifier.size((size / 2).dp),
        )
    }
}

private fun List<Visit>.filterByDateRange(
    fromDate: LocalDate?,
    toDate: LocalDate?,
): List<Visit> =
    filter { visit ->
        val visitDate = visit.startsAt.toLocalDateOrNull()
        visitDate != null &&
            (fromDate == null || !visitDate.isBefore(fromDate)) &&
            (toDate == null || !visitDate.isAfter(toDate))
    }

private fun Long.toDateLabel(): String = toLocalDate().format(FilterDateFormatter)

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()

private fun String.toLocalDateOrNull(): LocalDate? =
    runCatching { LocalDateTime.parse(this).toLocalDate() }
        .recoverCatching { LocalDate.parse(substringBefore("T")) }
        .getOrNull()

private fun String.toVisitDateLabel(): String =
    runCatching { LocalDateTime.parse(this).format(VisitDateFormatter) }
        .recoverCatching { substringBefore("T").split("-").let { "${it[2]}-${it[1]}-${it[0]}" } }
        .getOrDefault(this)

private val VisitDescription.titleRes: Int
    get() =
        when (this) {
            VisitDescription.CHECKUP -> R.string.visit_type_checkup
            VisitDescription.VACCINATION -> R.string.visit_type_vaccination
            VisitDescription.EMERGENCY -> R.string.visit_type_emergency
            VisitDescription.PREVENTIVE_CARE -> R.string.visit_type_prevention
            VisitDescription.SPECIALIST_CONSULTATION -> R.string.visit_type_specialist_consultation
            VisitDescription.DIAGNOSTIC_EXAM -> R.string.visit_type_diagnostic_exam
            VisitDescription.SURGICAL_PROCEDURE -> R.string.visit_type_surgery
            VisitDescription.DENTAL_PROCEDURE -> R.string.visit_type_dental_procedure
        }
private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private val VisitDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
private val FilterDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
