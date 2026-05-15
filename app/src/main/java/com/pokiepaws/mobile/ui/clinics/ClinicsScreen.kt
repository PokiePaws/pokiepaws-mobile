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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieWhite

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private const val SEARCH_BAR_OFFSET = -24

@Composable
fun ClinicsScreen(
    onClinicClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClinicsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.load() }

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
                    .padding(top = HEADER_TOP_PADDING.dp, bottom = (HEADER_BOTTOM_PADDING + 12).dp)
                    .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "Kliniki",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PokieWhite,
            )
        }
        ClinicSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = SEARCH_BAR_OFFSET.dp),
        )
        Box(modifier = Modifier.offset(y = SEARCH_BAR_OFFSET.dp)) {
            ClinicsContent(
                state = state,
                searchQuery = searchQuery,
                onClinicClick = onClinicClick,
            )
        }
    }
}

@Composable
private fun ClinicSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Szukaj kliniki...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Szukaj", tint = PokieBlue) },
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
private fun ClinicsContent(
    state: ClinicsUiState,
    searchQuery: String,
    onClinicClick: (Long) -> Unit,
) {
    when (state) {
        ClinicsUiState.Loading -> LoadingView()
        is ClinicsUiState.Error -> MessageView(text = stringResource(R.string.error_with_message, state.message))
        is ClinicsUiState.Success ->
            ClinicsResult(
                state = state,
                searchQuery = searchQuery,
                onClinicClick = onClinicClick,
            )
    }
}

@Composable
private fun ClinicsResult(
    state: ClinicsUiState.Success,
    searchQuery: String,
    onClinicClick: (Long) -> Unit,
) {
    val filtered =
        remember(state.clinics, searchQuery) {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) {
                state.clinics
            } else {
                state.clinics.filter { clinic ->
                    clinic.clinicName.lowercase().contains(q) ||
                        clinic.street.lowercase().contains(q) ||
                        clinic.city.lowercase().contains(q)
                }
            }
        }

    when {
        state.clinics.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🏥", fontSize = 64.sp)
                    Text(
                        text = stringResource(R.string.clinics_empty),
                        fontWeight = FontWeight.Bold,
                        color = PokieBlueDark,
                    )
                }
            }
        }

        filtered.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔍", fontSize = 64.sp)
                    Text(
                        text = "Brak wyników dla \"$searchQuery\"",
                        fontWeight = FontWeight.Bold,
                        color = PokieBlueDark,
                    )
                }
            }
        }

        else -> {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(filtered, key = { it.id }) { clinic ->
                    ClinicCard(
                        clinic = clinic,
                        onClick = { onClinicClick(clinic.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageView(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}

@Composable
private fun ClinicCard(
    clinic: Clinic,
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
                        .background(Color(0xFFF0F8FA)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🏥", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = clinic.clinicName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                )
                Text(
                    text = "${clinic.street} ${clinic.houseNumber}, ${clinic.postalCode} ${clinic.city}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
                clinic.phone?.let {
                    Text(
                        text = stringResource(R.string.clinic_phone, it),
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
