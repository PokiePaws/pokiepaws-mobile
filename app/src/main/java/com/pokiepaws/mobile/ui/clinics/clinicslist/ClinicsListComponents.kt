package com.pokiepaws.mobile.ui.clinics.clinicslist

import androidx.annotation.StringRes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
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
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.util.theme.PokieBlue
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite

@Composable
fun ClinicSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes placeholderRes: Int = R.string.clinic_search_placeholder,
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
            placeholder = { Text(stringResource(placeholderRes), color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_content_description),
                    tint = PokieBlue,
                )
            },
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
fun ClinicsContent(
    state: ClinicsUiState,
    searchQuery: String,
    onClinicClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
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
}

@Composable
fun ClinicsResult(
    state: ClinicsUiState.Success,
    searchQuery: String,
    onClinicClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
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
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BlueClinicIcon(icon = Icons.Default.LocalHospital, size = 64)
                    Text(
                        text = stringResource(R.string.clinics_empty),
                        fontWeight = FontWeight.Bold,
                        color = PokieBlueDark,
                    )
                }
            }
        }
        filtered.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BlueClinicIcon(icon = Icons.Default.Search, size = 64)
                    Text(
                        text = stringResource(R.string.clinics_no_results, searchQuery),
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
                modifier = modifier.fillMaxSize(),
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
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
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
fun ClinicCard(
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
            BlueClinicIcon(icon = Icons.Default.LocalHospital, size = 72)
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

@Composable
private fun BlueClinicIcon(
    icon: ImageVector,
    size: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(size.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ClinicIconBackground),
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

private val ClinicIconBackground = Color(0xFFE3F6FC)
