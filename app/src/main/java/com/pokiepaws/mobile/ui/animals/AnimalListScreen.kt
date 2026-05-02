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

private val ANIMAL_AVATAR_BG = Color(0xFFF0F8FA)
private const val HEADER_ROUNDING = 32
private const val CARD_ROUNDING = 24
private const val AVATAR_ROUNDING = 16
private const val BUTTON_ROUNDING = 12
private const val AVATAR_SIZE = 72
private const val ADD_BUTTON_SIZE = 48
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private const val LIST_SPACING = 16
private const val CARD_ELEVATION = 4
private const val FONT_SIZE_TITLE = 24
private const val FONT_SIZE_NAME = 18
private const val FONT_SIZE_EMOJI = 36
private const val EMPTY_STATE_EMOJI_SIZE = 64

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
        AnimalListHeader(onAddAnimal)

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
                    AnimalLazyList(
                        animals = state.animals,
                        onAnimalClick = onAnimalClick,
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun AnimalListHeader(onAddAnimal: () -> Unit) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Moje zwierzęta",
                fontSize = FONT_SIZE_TITLE.sp,
                fontWeight = FontWeight.Bold,
                color = PokieWhite,
            )

            IconButton(
                onClick = onAddAnimal,
                modifier =
                    Modifier
                        .size(ADD_BUTTON_SIZE.dp)
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
}

@Composable
private fun AnimalLazyList(
    animals: List<Animal>,
    onAnimalClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(LIST_SPACING.dp),
        contentPadding = PaddingValues(all = 24.dp),
    ) {
        items(animals) { animal ->
            AnimalCard(
                animal = animal,
                onClick = { onAnimalClick(animal.id) },
            )
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
        shape = RoundedCornerShape(CARD_ROUNDING.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(CARD_ELEVATION.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(AVATAR_SIZE.dp)
                        .clip(RoundedCornerShape(AVATAR_ROUNDING.dp))
                        .background(ANIMAL_AVATAR_BG),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🐾", fontSize = FONT_SIZE_EMOJI.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.name,
                    fontSize = FONT_SIZE_NAME.sp,
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
            Text(text = "🐾", fontSize = EMPTY_STATE_EMOJI_SIZE.sp)
            Text(
                text = "Nie masz jeszcze żadnych zwierzaków",
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
            Button(
                onClick = onAddAnimal,
                modifier = Modifier.padding(top = 16.dp),
                shape = RoundedCornerShape(BUTTON_ROUNDING.dp),
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
