package com.pokiepaws.mobile.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.ui.visits.isUpcomingVisitStatus
import com.pokiepaws.mobile.ui.visits.localizedVisitStatusLabel
import com.pokiepaws.mobile.ui.visits.visitlist.VisitListUiState
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieRed
import com.pokiepaws.mobile.util.theme.PokieWhite
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreenContent(
    visitState: VisitListUiState,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToAppointments: () -> Unit = {},
    onVisitClick: (Long) -> Unit = {},
    animalNamesById: HomeAnimalNames = HomeAnimalNames(),
) {
    val scrollState = rememberScrollState()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onSecondary),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
        ) {
            HomeHeader(onNavigateToNotifications)

            DrTuptusImage(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 10.dp),
            )

            Column(modifier = Modifier.padding(horizontal = 36.dp)) {
                SectionHeader(
                    title = stringResource(R.string.home_upcoming_visit),
                    onClick = onNavigateToAppointments,
                    textColor = PokieBlueDark,
                )

                UpcomingVisitCard(
                    visitState = visitState,
                    animalNamesById = animalNamesById,
                    onVisitClick = onVisitClick,
                )

                Spacer(modifier = Modifier.height(SECTION_SPACING.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader(onNotificationsClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape =
                        RoundedCornerShape(
                            bottomStart = CORNER_RADIUS_HEADER.dp,
                            bottomEnd = CORNER_RADIUS_HEADER.dp,
                        ),
                )
                .padding(bottom = 40.dp, top = 16.dp)
                .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_welcome),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PokieWhite,
            )
            IconButton(
                onClick = onNotificationsClick,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(PokieWhite.copy(alpha = 0.2f)),
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.notifications_title),
                    tint = PokieWhite,
                )
            }
        }
    }
}

@Composable
private fun DrTuptusImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.dr_tuptus),
            contentDescription = stringResource(R.string.dr_tuptus_content_description),
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .fillMaxWidth(WIDTH)
                    .height(170.dp),
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    onClick: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
        androidx.compose.material3.TextButton(onClick = onClick) {
            Text(
                text = stringResource(R.string.view_all),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun UpcomingVisitCard(
    visitState: VisitListUiState,
    animalNamesById: HomeAnimalNames,
    onVisitClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        when (visitState) {
            VisitListUiState.Loading -> {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VisitIcon(icon = Icons.Default.CalendarMonth)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.upcoming_visit_loading),
                            fontWeight = FontWeight.Bold,
                            color = PokieBlueDark,
                        )
                        Text(
                            text = stringResource(R.string.upcoming_visit_loading_description),
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                    }
                    CircularProgressIndicator(modifier = Modifier.size(22.dp))
                }
            }

            is VisitListUiState.Error -> {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VisitIcon(icon = Icons.Default.Warning)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.upcoming_visit_loading_error),
                            fontWeight = FontWeight.Bold,
                            color = PokieRed,
                        )
                        Text(visitState.message, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            is VisitListUiState.Success -> {
                val upcomingVisits =
                    visitState.visits
                        .filter { it.status.isUpcomingVisitStatus() }
                        .sortedBy { it.startsAt }
                if (upcomingVisits.isEmpty()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        VisitIcon(icon = Icons.Default.CalendarMonth)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.upcoming_visit_empty),
                                fontWeight = FontWeight.Bold,
                                color = PokieBlueDark,
                            )
                            Text(
                                text = stringResource(R.string.upcoming_visit_empty_description),
                                color = Color.Gray,
                                fontSize = 14.sp,
                            )
                        }
                    }
                } else {
                    Column {
                        upcomingVisits.forEachIndexed { index, visit ->
                            UpcomingVisitContent(
                                visit = visit,
                                animalName = animalNamesById.items[visit.animalId],
                                onClick = { onVisitClick(visit.id) },
                            )
                            if (index < upcomingVisits.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingVisitContent(
    visit: Visit,
    animalName: String?,
    onClick: () -> Unit,
) {
    val dateTime = parseIsoLocalDateTimeOrNull(visit.startsAt)
    val dayLabel =
        dateTime?.let { formatDayLabel(it.toLocalDate()) }
            ?: stringResource(R.string.upcoming_visit_fallback_day)
    val timeLabel = dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--"

    Row(
        modifier =
            Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VisitIcon(icon = Icons.Default.CalendarMonth)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = localizedVisitStatusLabel(visit.status),
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
            Text(
                text = animalName ?: stringResource(R.string.animal_unknown_name),
                color = PokieBlueDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
            Text(
                text = stringResource(visit.description.titleRes),
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

@Composable
private fun VisitIcon(icon: ImageVector) {
    Box(
        modifier =
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(APPOINTMENT_ICON_BG),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PokieBlueDark,
            modifier = Modifier.size(28.dp),
        )
    }
}

private fun parseIsoLocalDateTimeOrNull(value: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(value)
    } catch (_: Exception) {
        null
    }
}

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

@Composable
private fun formatDayLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> stringResource(R.string.today_label)
        today.plusDays(1) -> stringResource(R.string.tomorrow_label)
        else -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}

private val APPOINTMENT_ICON_BG = Color(0xFFE3F6FC)
private val APPOINTMENT_CARD_BG = Color(0xFFF0F8FA)
private const val CORNER_RADIUS_HEADER = 18
private const val SECTION_SPACING = 24

private const val WIDTH = 0.72f
