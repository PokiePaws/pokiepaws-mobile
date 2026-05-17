package com.pokiepaws.mobile.ui.clinics

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieWhite

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32

@Composable
fun VetListScreen(
    clinicId: Long,
    onBack: () -> Unit,
    onVetClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VetsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(clinicId) { viewModel.load(clinicId) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        // ── Blue header ────────────────────────────────────────────────────
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
                    .padding(top = HEADER_TOP_PADDING.dp, bottom = (HEADER_BOTTOM_PADDING + 12).dp)
                    .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Strzałka — identyczna jak w AnimalScreen
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

        // ── Content ────────────────────────────────────────────────────────
        VetsContent(
            state = state,
            onVetClick = onVetClick,
        )
    }
}

@Composable
private fun VetsContent(
    state: VetsUiState,
    onVetClick: (Long) -> Unit,
) {
    when (state) {
        VetsUiState.Loading -> LoadingView()
        is VetsUiState.Error -> MessageView(text = stringResource(R.string.error_with_message, state.message))
        is VetsUiState.Success -> VetsResult(state = state, onVetClick = onVetClick)
    }
}

@Composable
private fun VetsResult(
    state: VetsUiState.Success,
    onVetClick: (Long) -> Unit,
) {
    if (state.vets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "👨‍⚕️", fontSize = 64.sp)
                Text(
                    text = stringResource(R.string.vets_empty),
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.vets, key = { it.userId }) { vet ->
                VetCard(vet = vet, onClick = { onVetClick(vet.userId) })
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PokieBlue)
    }
}

@Composable
private fun MessageView(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}

@Composable
private fun VetCard(
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
            Box(
                modifier =
                    Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "👨‍⚕️", fontSize = 32.sp)
            }

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
