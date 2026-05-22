package com.pokiepaws.mobile.ui.clinics.clinicslist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.util.theme.PokieWhite

@Composable
fun ClinicsListScreen(
    onClinicClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClinicsListViewModel = hiltViewModel(),
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
                    .padding(top = HEADER_TOP_PADDING.dp, bottom = (HEADER_BOTTOM_PADDING + 36).dp)
                    .padding(horizontal = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.clinics),
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
private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private const val SEARCH_BAR_OFFSET = -24