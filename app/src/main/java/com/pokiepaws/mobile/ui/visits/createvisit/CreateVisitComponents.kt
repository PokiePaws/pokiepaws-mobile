package com.pokiepaws.mobile.ui.visits.createvisit

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Snackbar
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
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.CreateVisitStep
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.ui.clinics.clinicslist.ClinicSearchBar
import com.pokiepaws.mobile.util.theme.PokieBlue
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val TIME_LABEL_LENGTH = 5
private const val SUMMARY_SLOT_LABEL_LENGTH = 17
private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private const val SEARCH_BAR_OFFSET = -24
private val CreateVisitIconBackground = Color(0xFFE3F6FC)
private val VisitDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

@Composable
fun CreateVisitContent(
    state: CreateVisitUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onStepBack: () -> Unit,
    onSelectClinic: (Clinic) -> Unit,
    onSelectVet: (Vet) -> Unit,
    onSelectDate: (String) -> Unit,
    onSelectSlot: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onClearError: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        .padding(top = HEADER_TOP_PADDING.dp, bottom = (HEADER_BOTTOM_PADDING + 36).dp)
                        .padding(horizontal = 24.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (state.step == CreateVisitStep.SELECT_CLINIC) {
                                onBack()
                            } else {
                                onStepBack()
                            }
                        },
                        modifier = Modifier.offset(x = (-12).dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_description),
                            tint = PokieWhite,
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(state.step.titleRes),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PokieWhite,
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                CreateVisitStepContent(
                    state = state,
                    onSelectClinic = onSelectClinic,
                    onSelectVet = onSelectVet,
                    onSelectDate = onSelectDate,
                    onSelectSlot = onSelectSlot,
                    onDescriptionChange = onDescriptionChange,
                    onConfirm = onConfirm,
                )

                ErrorSnackbar(
                    error = state.error,
                    onDismiss = onClearError,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun CreateVisitStepContent(
    state: CreateVisitUiState,
    onSelectClinic: (Clinic) -> Unit,
    onSelectVet: (Vet) -> Unit,
    onSelectDate: (String) -> Unit,
    onSelectSlot: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    var clinicSearchQuery by rememberSaveable { mutableStateOf("") }
    var vetSearchQuery by rememberSaveable { mutableStateOf("") }

    when (state.step) {
        CreateVisitStep.SELECT_ANIMAL,
        CreateVisitStep.SELECT_CLINIC,
        ->
            LoadingOrContent(isLoading = state.isLoading) {
                ClinicListStep(
                    clinics = ClinicItems(state.clinics),
                    searchQuery = clinicSearchQuery,
                    onSearchQueryChange = { clinicSearchQuery = it },
                    onSelect = onSelectClinic,
                )
            }

        CreateVisitStep.SELECT_VET ->
            LoadingOrContent(isLoading = state.isLoading) {
                VetListStep(
                    vets = VetItems(state.vets),
                    searchQuery = vetSearchQuery,
                    onSearchQueryChange = { vetSearchQuery = it },
                    onSelect = onSelectVet,
                )
            }

        CreateVisitStep.SELECT_SLOT ->
            SlotStep(
                selectedDate = state.selectedDate,
                slots = SlotItems(state.availableSlots),
                isLoading = state.isLoading,
                onDateSelected = onSelectDate,
                onSlotSelected = onSelectSlot,
            )

        CreateVisitStep.CONFIRM ->
            ConfirmStep(
                state = state,
                description = state.description,
                onDescriptionChange = onDescriptionChange,
                onConfirm = onConfirm,
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
            CircularProgressIndicator(color = PokieBlue)
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
                    Text(stringResource(R.string.ok_button))
                }
            },
        ) { Text(err) }
    }
}

private val CreateVisitStep.titleRes: Int
    get() =
        when (this) {
            CreateVisitStep.SELECT_CLINIC -> R.string.create_visit_select_clinic
            CreateVisitStep.SELECT_VET -> R.string.create_visit_select_vet
            CreateVisitStep.SELECT_SLOT -> R.string.create_visit_select_slot
            CreateVisitStep.SELECT_ANIMAL -> R.string.create_visit_select_animal
            CreateVisitStep.CONFIRM -> R.string.create_visit_confirm
        }

@Composable
private fun EmptyVisitState(
    icon: ImageVector,
    message: String,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BlueVisitIcon(icon = icon, size = 64)
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
        }
    }
}

@Composable
private fun ClinicListStep(
    clinics: ClinicItems,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelect: (Clinic) -> Unit,
) {
    if (clinics.items.isEmpty()) {
        EmptyVisitState(
            icon = Icons.Default.LocalHospital,
            message = stringResource(R.string.create_visit_clinics_empty),
        )
        return
    }

    val filteredClinics = rememberFilteredClinics(clinics, searchQuery)

    Column(modifier = Modifier.fillMaxSize()) {
        ClinicSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier =
                Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .offset(y = SEARCH_BAR_OFFSET.dp),
        )

        if (filteredClinics.isEmpty()) {
            EmptyVisitState(
                icon = Icons.Default.Search,
                message = stringResource(R.string.clinics_no_results, searchQuery),
            )
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filteredClinics, key = { it.id }) { clinic ->
                Card(
                    onClick = { onSelect(clinic) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PokieWhite),
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BlueVisitIcon(icon = Icons.Default.LocalHospital, size = 72)

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = clinic.clinicName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PokieBlueDark,
                            )
                            Text(
                                text = "${clinic.street} ${clinic.houseNumber}, ${clinic.city}",
                                fontSize = 13.sp,
                                color = Color.Gray,
                            )
                            clinic.phone?.let { phone ->
                                Text(
                                    text = stringResource(R.string.clinic_phone, phone),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 4.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VetListStep(
    vets: VetItems,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelect: (Vet) -> Unit,
) {
    if (vets.items.isEmpty()) {
        EmptyVisitState(
            icon = Icons.Default.Person,
            message = stringResource(R.string.create_visit_vets_empty),
        )
        return
    }

    val filteredVets = rememberFilteredVets(vets, searchQuery)

    Column(modifier = Modifier.fillMaxSize()) {
        ClinicSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholderRes = R.string.vet_search_placeholder,
            modifier =
                Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .offset(y = SEARCH_BAR_OFFSET.dp),
        )

        if (filteredVets.isEmpty()) {
            EmptyVisitState(
                icon = Icons.Default.Search,
                message = stringResource(R.string.vets_no_results, searchQuery),
            )
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filteredVets, key = { it.userId }) { vet ->
                Card(
                    onClick = { onSelect(vet) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PokieWhite),
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BlueVisitIcon(icon = Icons.Default.Person, size = 72)

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${vet.firstName} ${vet.lastName}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PokieBlueDark,
                            )
                            vet.specialization?.let {
                                Text(
                                    text = it,
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BlueVisitIcon(
    icon: ImageVector,
    size: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(size.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CreateVisitIconBackground),
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

@Composable
private fun rememberFilteredClinics(
    clinics: ClinicItems,
    searchQuery: String,
): List<Clinic> =
    remember(clinics.items, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            clinics.items
        } else {
            clinics.items.filter { clinic ->
                clinic.clinicName.lowercase().contains(q) ||
                    clinic.street.lowercase().contains(q) ||
                    clinic.city.lowercase().contains(q)
            }
        }
    }

@Composable
private fun rememberFilteredVets(
    vets: VetItems,
    searchQuery: String,
): List<Vet> =
    remember(vets.items, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            vets.items
        } else {
            vets.items.filter { vet ->
                vet.firstName.lowercase().contains(q) ||
                    vet.lastName.lowercase().contains(q) ||
                    vet.specialization.orEmpty().lowercase().contains(q)
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SlotStep(
    selectedDate: String?,
    slots: SlotItems,
    isLoading: Boolean,
    onDateSelected: (String) -> Unit,
    onSlotSelected: (String) -> Unit,
) {
    val today = remember { LocalDate.now() }
    val selectableDates =
        remember(today) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = !localDateFromMillis(utcTimeMillis).isBefore(today)

                override fun isSelectableYear(year: Int): Boolean = year >= today.year
            }
        }
    val selectedDateMillis = remember(selectedDate) { selectedDate?.toDateMillisOrNull() }
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
            selectableDates = selectableDates,
        )
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis
                        ?.let(::localDateFromMillis)
                        ?.format(VisitDateFormatter)
                        ?.let(onDateSelected)
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
        ) { DatePicker(state = datePickerState) }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = PokieWhite),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = PokieBlue,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text =
                        selectedDate
                            ?: stringResource(R.string.create_visit_date_placeholder),
                    color = if (selectedDate != null) PokieBlueDark else Color.Gray,
                    fontWeight = if (selectedDate != null) FontWeight.Medium else FontWeight.Normal,
                )
            }
        }

        when {
            isLoading ->
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PokieBlue)
                }

            slots.items.isEmpty() && selectedDate != null ->
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.create_visit_no_slots),
                        color = Color.Gray,
                    )
                }

            slots.items.isNotEmpty() -> {
                Text(
                    text = stringResource(R.string.create_visit_available_hours),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(slots.items) { slot ->
                        val time = slot.substringAfter("T").take(TIME_LABEL_LENGTH)
                        Card(
                            onClick = { onSlotSelected(slot) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PokieWhite),
                            elevation = CardDefaults.cardElevation(3.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = time,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PokieBlueDark,
                                )
                            }
                        }
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
                .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PokieWhite),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SummaryRow(
                    label = stringResource(R.string.create_visit_summary_clinic),
                    value = state.selectedClinic?.clinicName ?: "",
                )
                SummaryDivider()
                SummaryRow(
                    label = stringResource(R.string.create_visit_summary_address),
                    value =
                        state.selectedClinic?.let {
                            "${it.street} ${it.houseNumber}, ${it.city}"
                        } ?: "",
                )
                SummaryDivider()
                SummaryRow(
                    label = stringResource(R.string.create_visit_summary_vet),
                    value =
                        state.selectedVet?.let {
                            "${it.firstName} ${it.lastName}"
                        } ?: "",
                )
                SummaryDivider()
                SummaryRow(
                    label = stringResource(R.string.create_visit_summary_date),
                    value =
                        state.selectedSlot
                            ?.replace("T", "  ")
                            ?.take(SUMMARY_SLOT_LABEL_LENGTH) ?: "",
                )
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.create_visit_description_label)) },
            placeholder = { Text(stringResource(R.string.create_visit_description_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(16.dp),
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = PokieBlue,
                    contentColor = PokieWhite,
                ),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PokieWhite,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(R.string.create_visit_submit),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
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
            color = Color.Gray,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = PokieBlueDark,
        )
    }
}

@Composable
private fun SummaryDivider() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

private fun String.toDateMillisOrNull(): Long? =
    runCatching {
        LocalDate.parse(this, VisitDateFormatter)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }.getOrNull()

private fun localDateFromMillis(utcTimeMillis: Long): LocalDate =
    Instant.ofEpochMilli(utcTimeMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
