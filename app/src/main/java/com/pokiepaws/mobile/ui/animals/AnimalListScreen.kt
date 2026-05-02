package com.pokiepaws.mobile.ui.animals

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pokiepaws.mobile.domain.model.Animal
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieWhite

@Composable
fun AnimalListScreen(
    onAddAnimal: () -> Unit,
    onAnimalClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    )
                    .padding(top = 48.dp, bottom = 32.dp)
                    .padding(horizontal = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Moje zwierzęta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieWhite,
                )

                IconButton(
                    onClick = onAddAnimal,
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PokieWhite.copy(alpha = 0.2f)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Dodaj zwierzę",
                        tint = PokieWhite,
                    )
                }
            }
        }

        when (val state = uiState) {
            is AnimalUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is AnimalUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadAnimals() },
                )
            }

            is AnimalUiState.Success -> {
                if (state.animals.isEmpty()) {
                    EmptyAnimalsView(onAddAnimal)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding =
                            PaddingValues(
                                horizontal = 24.dp,
                                vertical = 24.dp,
                            ),
                    ) {
                        items(state.animals) { animal ->
                            AnimalCard(
                                animal = animal,
                                onClick = { onAnimalClick(animal.id) },
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun AnimalCard(
    animal: Animal,
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
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F8FA)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🐾", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
                Text(
                    text = "${animal.species}${animal.breed?.let { " • $it" } ?: ""}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                )

                animal.birthDate?.let { date ->
                    Text(
                        text = "Ur. $date",
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

@Composable
fun EmptyAnimalsView(
    onAddAnimal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🐾", fontSize = 64.sp)
            Text(
                text = "Nie masz jeszcze żadnych zwierzaków",
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
            Button(
                onClick = onAddAnimal,
                modifier = Modifier.padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Dodaj pierwszego towarzysza")
            }
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Wystąpił błąd", color = Color.Red, fontWeight = FontWeight.Bold)
            Text(
                text = message,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray,
            )
            Button(onClick = onRetry) { Text("Spróbuj ponownie") }
        }
    }
}
