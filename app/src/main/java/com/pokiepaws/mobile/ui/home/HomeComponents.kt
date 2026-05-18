package com.pokiepaws.mobile.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.ui.animals.addanimal.animalSpeciesLabel
import com.pokiepaws.mobile.ui.animals.animallist.AnimalListUiState
import com.pokiepaws.mobile.ui.visits.visitlist.VisitListUiState
import com.pokiepaws.mobile.util.theme.PokieBlue
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieBlueLight
import com.pokiepaws.mobile.util.theme.PokieRed
import com.pokiepaws.mobile.util.theme.PokieWhite
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val PET_AVATAR_BG = Color(0xFFF0F8FA)
private val APPOINTMENT_ICON_BG = Color(0xFFE3F6FC)
private val APPOINTMENT_CARD_BG = Color(0xFFF0F8FA)
private const val ADD_PET_BORDER_ALPHA = 0.5f
private const val CORNER_RADIUS_HEADER = 18
private const val CORNER_RADIUS_CARD = 16
private const val CORNER_RADIUS_PET_CARD = 24
private const val OFFSET_SEARCH_BAR = -24
private const val PET_CARD_WIDTH = 140
private const val PET_CARD_HEIGHT = 160
private const val AVATAR_SIZE = 70
private const val ICON_SIZE_LARGE = 56
private const val QUICK_ACTION_ICON_SIZE = 50
private const val SECTION_SPACING = 24
private const val QUICK_ACTION_WEIGHT_PRIMARY = 1.5f

@Composable
fun HomeScreenContent(
    animalState: AnimalListUiState,
    visitState: VisitListUiState,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToAnimals: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToClinics: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }

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

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
            )

            Column(modifier = Modifier.padding(horizontal = 36.dp)) {
                SectionHeader(
                    title = "Upcoming visit",
                    onClick = onNavigateToAppointments,
                    textColor = PokieBlueDark,
                )

                UpcomingVisitCard(
                    visitState = visitState,
                    onClick = onNavigateToAppointments,
                )

                Spacer(modifier = Modifier.height(SECTION_SPACING.dp))

                Text(
                    text = "Quick actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(
                        modifier = Modifier.weight(QUICK_ACTION_WEIGHT_PRIMARY),
                        icon = Icons.Default.CalendarMonth,
                        label = "Schedule your visit",
                        backgroundColor = PokieWhite.copy(alpha = 0.2f),
                        onClick = onNavigateToClinics,
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        label = "Add an animal",
                        backgroundColor = PokieWhite.copy(alpha = 0.2f),
                        onClick = onNavigateToAnimals,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionHeader(
                    title = "My animals",
                    onClick = onNavigateToAnimals,
                    textColor = PokieBlueDark,
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                ) {
                    when (val state = animalState) {
                        is AnimalListUiState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                        }
                        is AnimalListUiState.Success -> {
                            items(state.animals) { animal ->
                                PetCard(
                                    name = animal.name,
                                    type = animalSpeciesLabel(animal.species),
                                    emoji = "🐾",
                                    onClick = onNavigateToAnimals,
                                )
                            }
                        }
                        is AnimalListUiState.Error -> {
                            item { Text("Loading error", color = PokieRed) }
                        }
                    }
                    item { AddPetCard(onClick = onNavigateToAnimals) }
                }
                Spacer(modifier = Modifier.height(32.dp))
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
                text = "Welcome to PokiePaws 🐾",
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
                Icon(Icons.Default.Notifications, "Notifications", tint = PokieWhite)
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = OFFSET_SEARCH_BAR.dp),
        shape = RoundedCornerShape(CORNER_RADIUS_CARD.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search for a clinic...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = PokieBlue) },
            colors =
                TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
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
        TextButton(onClick = onClick) {
            Text("View all", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PetCard(
    name: String,
    type: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .size(width = PET_CARD_WIDTH.dp, height = PET_CARD_HEIGHT.dp)
                .background(Color.Transparent),
        onClick = onClick,
        shape = RoundedCornerShape(CORNER_RADIUS_PET_CARD.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(AVATAR_SIZE.dp)
                        .clip(CircleShape)
                        .background(PET_AVATAR_BG),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, fontSize = 36.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, fontWeight = FontWeight.Bold, color = PokieBlueDark, fontSize = 16.sp)
            Text(type, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AppointmentCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(CORNER_RADIUS_PET_CARD.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(ICON_SIZE_LARGE.dp)
                        .clip(CircleShape)
                        .background(APPOINTMENT_ICON_BG),
                contentAlignment = Alignment.Center,
            ) {
                Text("🐾", fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Vaccination (Boguś)", fontWeight = FontWeight.Bold, color = PokieBlueDark)
                Text("Dr. Jan Kowalski", color = Color.Gray, fontSize = 14.sp)
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
                        text = "Tomorrow",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("14:30", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun AddPetCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .size(width = PET_CARD_WIDTH.dp, height = PET_CARD_HEIGHT.dp)
                .clickable { onClick() },
        shape = RoundedCornerShape(CORNER_RADIUS_PET_CARD.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border =
            BorderStroke(
                2.dp,
                Color.LightGray.copy(alpha = ADD_PET_BORDER_ALPHA),
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Add", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(CORNER_RADIUS_PET_CARD.dp),
        colors = CardDefaults.cardColors(containerColor = PokieBlueLight),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(QUICK_ACTION_ICON_SIZE.dp)
                        .clip(CircleShape)
                        .background(backgroundColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = PokieWhite)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = PokieWhite)
        }
    }
}

@Composable
fun UpcomingVisitCard(
    visitState: VisitListUiState,
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
            VisitListUiState.Loading -> {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VisitIcon(text = "đźľ")
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
                    VisitIcon(text = "âš ď¸Ź", fontSize = 22)
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
                val next = visitState.visits.minByOrNull { it.startsAt }
                if (next == null) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        VisitIcon(text = "đźľ")
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
                    UpcomingVisitContent(visit = next)
                }
            }
        }
    }
}

@Composable
private fun UpcomingVisitContent(visit: Visit) {
    val dateTime = parseIsoLocalDateTimeOrNull(visit.startsAt)
    val dayLabel = dateTime?.let { formatDayLabel(it.toLocalDate()) } ?: stringResource(R.string.upcoming_visit_fallback_day)
    val timeLabel = dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--"

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VisitIcon(text = "đźľ")

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = visit.status.replace('_', ' '),
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
            Text(
                text = visit.description?.takeIf { it.isNotBlank() } ?: stringResource(R.string.upcoming_visit_details_hint),
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
private fun VisitIcon(
    text: String,
    fontSize: Int = 28,
) {
    Box(
        modifier =
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(APPOINTMENT_ICON_BG),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontSize = fontSize.sp)
    }
}

private fun parseIsoLocalDateTimeOrNull(value: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(value)
    } catch (_: Exception) {
        null
    }
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
