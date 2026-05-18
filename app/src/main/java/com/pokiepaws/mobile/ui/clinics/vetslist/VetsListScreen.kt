package com.pokiepaws.mobile.ui.clinics.vetslist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 32

@Composable
fun VetsListScreen(
    clinicId: Long,
    onBack: () -> Unit,
    onVetClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VetsListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

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
                    .padding(top = HEADER_TOP_PADDING.dp, bottom = (HEADER_BOTTOM_PADDING + 12).dp)
                    .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            BackHeader(onBack = onBack)
        }
        VetsContent(
            state = state,
            onVetClick = onVetClick,
        )
    }
}
