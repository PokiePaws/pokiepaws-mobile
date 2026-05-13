package com.pokiepaws.mobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieRed
import com.pokiepaws.mobile.ui.theme.PokieWhite
import com.pokiepaws.mobile.ui.visits.VisitUiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val APPOINTMENT_ICON_BG = Color(0xFFE3F6FC)
private val APPOINTMENT_CARD_BG = Color(0xFFF0F8FA)

@Composable
fun UpcomingVisitCard(
    visitState: VisitUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        when (visitState) {
            VisitUiState.Loading -> {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(APPOINTMENT_ICON_BG),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🐾", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Loading...", fontWeight = FontWeight.Bold, color = PokieBlueDark)
                        Text("Fetching your upcoming visit", color = Color.Gray, fontSize = 14.sp)
                    }
                    CircularProgressIndicator(modifier = Modifier.size(22.dp))
                }
            }

            is VisitUiState.Error -> {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(APPOINTMENT_ICON_BG),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("⚠️", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Loading error", fontWeight = FontWeight.Bold, color = PokieRed)
                        Text(visitState.message, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            is VisitUiState.Success -> {
                val next = visitState.visits.minByOrNull { it.startsAt } // ISO => działa
                if (next == null) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(APPOINTMENT_ICON_BG),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("🐾", fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("No upcoming visits", fontWeight = FontWeight.Bold, color = PokieBlueDark)
                            Text("Schedule a visit in Clinics tab", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    UpcomingVisitContent(visit = next)
                }
            }
        }
    }
}

@Composable
private fun UpcomingVisitContent(visit: Visit) {
    val dateTime = parseIsoLocalDateTimeOrNull(visit.startsAt)
    val dayLabel = dateTime?.let { formatDayLabel(it.toLocalDate()) } ?: "Upcoming"
    val timeLabel = dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--"

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(APPOINTMENT_ICON_BG),
            contentAlignment = Alignment.Center,
        ) {
            Text("🐾", fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Nie masz jeszcze nazw (animalName/vetName), więc pokazujemy status + opis
            Text(
                text = visit.status.replace('_', ' '),
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
            Text(
                text = visit.description?.takeIf { it.isNotBlank() } ?: "Tap to see details",
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
            )
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = APPOINTMENT_CARD_BG),
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = dayLabel,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Text(timeLabel, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

private fun parseIsoLocalDateTimeOrNull(value: String): LocalDateTime? {
    return try {
        // backend: "2026-05-10T14:30:00"
        LocalDateTime.parse(value)
    } catch (_: Exception) {
        null
    }
}

private fun formatDayLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        else -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}
