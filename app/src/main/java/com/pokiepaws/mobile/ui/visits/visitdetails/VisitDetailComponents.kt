package com.pokiepaws.mobile.ui.visits.visitdetails

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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
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
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.PrescriptionItem
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun VisitDetailContent(
    state: VisitDetailUiState,
    isOnline: Boolean,
    onBack: () -> Unit,
    onCancelVisit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCancelDialog by remember { mutableStateOf(false) }

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
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onBack,
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .background(PokieWhite.copy(alpha = 0.2f)),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_content_description),
                        tint = PokieWhite,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.visit_details_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieWhite,
                )
            }
        }
        when (state) {
            is VisitDetailUiState.Loading ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            is VisitDetailUiState.Error ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.visit_error, state.message),
                        color = MaterialTheme.colorScheme.error,
                    )
                }

            is VisitDetailUiState.Success ->
                VisitDetailsBody(
                    visit = state.visit,
                    clinic = state.clinic,
                    prescription = state.prescription,
                    isPrescriptionLoading = state.isPrescriptionLoading,
                    prescriptionError = state.prescriptionError,
                    isOnline = isOnline,
                    onCancelClick = { showCancelDialog = true },
                )
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(stringResource(R.string.visit_cancel_dialog_title)) },
            text = { Text(stringResource(R.string.visit_cancel_dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelVisit()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                        ),
                ) {
                    Text(stringResource(R.string.visit_cancel_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(stringResource(R.string.close_button))
                }
            },
        )
    }
}

@Composable
private fun VisitDetailsBody(
    visit: Visit,
    clinic: Clinic?,
    prescription: Prescription?,
    isPrescriptionLoading: Boolean,
    prescriptionError: String?,
    isOnline: Boolean,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PokieWhite),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                BlueIconBox(
                    icon = if (visit.status == "CANCELLED") Icons.Default.Cancel else Icons.Default.CalendarMonth,
                    modifier = Modifier.size(72.dp),
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(visit.description.titleRes),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PokieBlueDark,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow(icon = Icons.Default.MedicalServices, text = visit.status)
                    InfoRow(icon = Icons.Default.CalendarMonth, text = visit.startsAt.toVisitDateLabel())
                    InfoRow(
                        icon = Icons.Default.Schedule,
                        text = stringResource(R.string.visit_start_time, visit.startsAt.toVisitTimeLabel()),
                    )
                    clinic?.let {
                        InfoRow(icon = Icons.Default.MedicalServices, text = it.clinicName)
                        InfoRow(icon = Icons.Default.Home, text = it.toAddressLabel())
                    }
                }
            }
        }
        val hasMedicalData =
            !visit.disease.isNullOrBlank() ||
                !visit.diagnosis.isNullOrBlank() ||
                !visit.recommendations.isNullOrBlank()

        if (hasMedicalData) {
            Text(
                text = stringResource(R.string.visit_medical_data_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PokieWhite),
                elevation = CardDefaults.cardElevation(4.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    visit.disease?.takeIf { it.isNotBlank() }?.let {
                        MedicalSection(icon = Icons.Default.Healing, label = stringResource(R.string.visit_disease_label), value = it)
                    }
                    visit.diagnosis?.takeIf { it.isNotBlank() }?.let {
                        MedicalSection(icon = Icons.Default.Science, label = stringResource(R.string.visit_diagnosis_label), value = it)
                    }
                    visit.recommendations?.takeIf { it.isNotBlank() }?.let {
                        MedicalSection(
                            icon = Icons.Default.Recommend,
                            label = stringResource(R.string.visit_recommendations_label),
                            value = it,
                        )
                    }
                }
            }
        }
        PrescriptionSection(
            prescription = prescription,
            isLoading = isPrescriptionLoading,
            error = prescriptionError,
        )

        if (visit.status == "SCHEDULED") {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onCancelClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = isOnline,
                shape = RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
            ) {
                Text(stringResource(R.string.visit_cancel_button), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PrescriptionSection(
    prescription: Prescription?,
    isLoading: Boolean,
    error: String?,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.visit_prescription_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PokieBlueDark,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PokieWhite),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    !error.isNullOrBlank() ->
                        Text(
                            text = stringResource(R.string.visit_prescription_error, error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    prescription == null ->
                        Text(
                            text = stringResource(R.string.visit_prescription_empty),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    else -> PrescriptionContent(prescription)
                }
            }
        }
    }
}

@Composable
private fun PrescriptionContent(prescription: Prescription) {
    prescription.creationDate?.let { date ->
        MedicalSection(
            icon = Icons.Default.CalendarMonth,
            label = stringResource(R.string.visit_prescription_creation_date),
            value = date.toDisplayDate(),
        )
    }
    prescription.recommendationDate?.let { date ->
        MedicalSection(
            icon = Icons.Default.Schedule,
            label = stringResource(R.string.visit_prescription_recommendation_date),
            value = date.toDisplayDate(),
        )
    }
    prescription.items.forEach { item ->
        PrescriptionItemSection(item)
    }
}

@Composable
private fun PrescriptionItemSection(item: PrescriptionItem) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                tint = PokieBlueDark,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.productName ?: stringResource(R.string.visit_prescription_unknown_product),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PokieBlueDark,
            )
        }
        PrescriptionItemDetail(label = stringResource(R.string.visit_prescription_quantity), value = item.quantityPackages?.toString())
        PrescriptionItemDetail(label = stringResource(R.string.visit_prescription_dosage), value = item.dosage)
        PrescriptionItemDetail(label = stringResource(R.string.visit_prescription_treatment_time), value = item.treatmentTime)
    }
}

@Composable
private fun PrescriptionItemDetail(
    label: String,
    value: String?,
) {
    if (!value.isNullOrBlank()) {
        Text(
            text = "$label: $value",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 22.dp, top = 2.dp),
        )
    }
}

@Composable
private fun BlueIconBox(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AvatarBg),
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

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PokieBlueDark,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 13.sp, color = Color.Gray)
    }
}

@Composable
private fun MedicalSection(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PokieBlueDark,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PokieBlueDark,
            )
        }
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 22.dp, top = 2.dp),
        )
    }
}

private fun String.toVisitDateLabel(): String =
    runCatching { LocalDateTime.parse(this).format(VisitDateFormatter) }
        .recoverCatching { substringBefore("T").split("-").let { "${it[2]}-${it[1]}-${it[0]}" } }
        .getOrDefault(this)

private fun String.toVisitTimeLabel(): String =
    runCatching { LocalDateTime.parse(this).format(VisitTimeFormatter) }
        .recoverCatching { substringAfter("T").take(VISIT_TIME_LENGTH) }
        .getOrDefault(this)

private fun String.toDisplayDate(): String =
    runCatching {
        split("-").let { "${it[2]}-${it[1]}-${it[0]}" }
    }.getOrDefault(this)

private fun Clinic.toAddressLabel(): String = "$street $houseNumber, $postalCode $city"

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
private const val VISIT_TIME_LENGTH = 5
private val AvatarBg = Color(0xFFF0F8FA)
private val VisitDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
private val VisitTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
