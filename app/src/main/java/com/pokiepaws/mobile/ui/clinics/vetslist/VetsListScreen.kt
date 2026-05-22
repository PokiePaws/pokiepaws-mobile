package com.pokiepaws.mobile.ui.clinics.vetslist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.ui.clinics.clinicslist.ClinicSearchBar

@Composable
fun VetsListScreen(
    clinicId: Long,
    onBack: () -> Unit,
    onVetClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VetsListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(clinicId) { viewModel.load(clinicId) }

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
            contentAlignment = Alignment.Center,
        ) {
            BackHeader(onBack = onBack)
        }
        ClinicSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderRes = R.string.vet_search_placeholder,
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = SEARCH_BAR_OFFSET.dp),
        )
        VetsContent(
            state = state,
            searchQuery = searchQuery,
            onVetClick = onVetClick,
            modifier = Modifier.offset(y = SEARCH_BAR_OFFSET.dp),
        )
    }
}

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32
private const val SEARCH_BAR_OFFSET = -24
