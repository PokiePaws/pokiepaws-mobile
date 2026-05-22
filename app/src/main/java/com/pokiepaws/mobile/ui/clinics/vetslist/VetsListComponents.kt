package com.pokiepaws.mobile.ui.clinics.vetslist

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.util.theme.PokieBlue
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite

@Composable
fun BackHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(
            onClick = onBack,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
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

        Text(
            text = stringResource(R.string.vets_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PokieWhite,
        )
    }
}

@Composable
fun VetsContent(
    state: VetsUiState,
    searchQuery: String,
    onVetClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (state) {
            VetsUiState.Loading -> LoadingView()
            is VetsUiState.Error -> MessageView(text = stringResource(R.string.error_with_message, state.message))
            is VetsUiState.Success ->
                VetsResult(
                    state = state,
                    searchQuery = searchQuery,
                    onVetClick = onVetClick,
                )
        }
    }
}

@Composable
fun VetsResult(
    state: VetsUiState.Success,
    searchQuery: String,
    onVetClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredVets =
        remember(state.vets, searchQuery) {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) {
                state.vets
            } else {
                state.vets.filter { vet ->
                    vet.firstName.lowercase().contains(q) ||
                        vet.lastName.lowercase().contains(q) ||
                        vet.specialization.orEmpty().lowercase().contains(q)
                }
            }
        }

    if (state.vets.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BlueVetIcon(icon = Icons.Default.Person, size = 64)
                Text(
                    text = stringResource(R.string.vets_empty),
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
            }
        }
    } else if (filteredVets.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BlueVetIcon(icon = Icons.Default.Search, size = 64)
                Text(
                    text = stringResource(R.string.vets_no_results, searchQuery),
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.fillMaxSize(),
        ) {
            items(filteredVets, key = { it.userId }) { vet ->
                VetCard(vet = vet, onClick = { onVetClick(vet.userId) })
            }
        }
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PokieBlue)
    }
}

@Composable
fun MessageView(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}

@Composable
fun VetCard(
    vet: Vet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BlueVetIcon(icon = Icons.Default.Person, size = 72)

            Spacer(modifier = Modifier.width(16.dp))

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

@Composable
private fun BlueVetIcon(
    icon: ImageVector,
    size: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(size.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(VetIconBackground),
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

private val VetIconBackground = Color(0xFFE3F6FC)
