package com.pokiepaws.mobile.ui.profile

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.ui.animals.AnimalUiState
import com.pokiepaws.mobile.ui.animals.AnimalViewModel
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieBlueLight
import com.pokiepaws.mobile.ui.theme.PokieRed
import com.pokiepaws.mobile.ui.theme.PokieWhite

@Composable
fun HomeScreen(
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToAnimals: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToClinics: () -> Unit = {},
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onNavigateToNotifications = onNavigateToNotifications,
        modifier = modifier,
        onNavigateToAnimals = onNavigateToAnimals,
        onNavigateToAppointments = onNavigateToAppointments,
        onNavigateToClinics = onNavigateToClinics,
    )
}

@Composable
fun HomeScreenContent(
    uiState: AnimalUiState,
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
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp),
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
                        onClick = onNavigateToNotifications,
                        modifier =
                            Modifier
                                .clip(CircleShape)
                                .background(PokieWhite.copy(alpha = 0.2f)),
                    ) {
                        Icon(Icons.Default.Notifications, "Notifications", tint = PokieWhite)
                    }
                }
            }
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .offset(y = (-24).dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = PokieWhite),
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
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

            Column(modifier = Modifier.padding(horizontal = 36.dp)) {
                SectionHeader(
                    title = "Upcoming visit",
                    onClick = onNavigateToAppointments,
                    textColor = PokieBlueDark,
                )

                AppointmentCard(onClick = onNavigateToAppointments)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Quick actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(
                        modifier = Modifier.weight(1.5f),
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
                    when (val state = uiState) {
                        is AnimalUiState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                        }
                        is AnimalUiState.Success -> {
                            items(state.animals) { animal ->
                                PetCard(
                                    name = animal.name,
                                    type = animal.species,
                                    emoji = "🐾",
                                    onClick = onNavigateToAnimals,
                                )
                            }
                        }
                        is AnimalUiState.Error -> {
                            item { Text("Loading error", color = PokieRed) }
                        }
                        else -> {}
                    }
                    item { AddPetCard(onClick = onNavigateToAnimals) }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
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
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                .size(width = 140.dp, height = 160.dp)
                .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F8FA)),
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
        shape = RoundedCornerShape(24.dp),
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
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F6FC)),
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FA)),
            ) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
                .size(width = 140.dp, height = 160.dp)
                .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color.LightGray.copy(alpha = 0.5f)),
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PokieBlueLight),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(50.dp)
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
