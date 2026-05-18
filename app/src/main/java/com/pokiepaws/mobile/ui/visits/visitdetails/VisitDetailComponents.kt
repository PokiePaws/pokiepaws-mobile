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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private val AvatarBg = Color(0xFFF0F8FA)

@Composable
fun VisitDetailContent(
    state: VisitDetailUiState,
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
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
        when (val s = state) {
            is VisitDetailUiState.Loading ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            is VisitDetailUiState.Error ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.visit_error, s.message),
                        color = MaterialTheme.colorScheme.error,
                    )
                }

            is VisitDetailUiState.Success ->
                VisitDetailsBody(
                    visit = s.visit,
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
                        showCancelDialog = false
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
                Box(
                    modifier =
                        Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AvatarBg),
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
                        text = stringResource(R.string.visit_card_title, visit.id),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PokieBlueDark,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow(label = "📋", text = visit.status)
                    InfoRow(label = "🕐", text = visit.startsAt.replace("T", " ").take(16))
                    InfoRow(label = "🕑", text = visit.endsAt.replace("T", " ").take(16))
                    visit.description?.takeIf { it.isNotBlank() }?.let {
                        InfoRow(label = "📝", text = it)
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
                        MedicalSection(emoji = "🦠", label = stringResource(R.string.visit_disease_label), value = it)
                    }
                    visit.diagnosis?.takeIf { it.isNotBlank() }?.let {
                        MedicalSection(emoji = "🔬", label = stringResource(R.string.visit_diagnosis_label), value = it)
                    }
                    visit.recommendations?.takeIf { it.isNotBlank() }?.let {
                        MedicalSection(emoji = "💊", label = stringResource(R.string.visit_recommendations_label), value = it)
                    }
                }
            }
        }
        if (visit.status == "SCHEDULED") {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onCancelClick,
                modifier = Modifier.fillMaxWidth(),
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
private fun InfoRow(
    label: String,
    text: String,
) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 13.sp, color = Color.Gray)
    }
}

@Composable
private fun MedicalSection(
    emoji: String,
    label: String,
    value: String,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
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
            modifier = Modifier.padding(start = 20.dp, top = 2.dp),
        )
    }
}
