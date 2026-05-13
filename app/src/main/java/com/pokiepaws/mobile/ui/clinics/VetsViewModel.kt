package com.pokiepaws.mobile.ui.clinics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.data.remote.service.VetApiService
import com.pokiepaws.mobile.domain.model.Vet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VetsViewModel @Inject constructor(
    private val api: VetApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<VetsUiState>(VetsUiState.Loading)
    val uiState: StateFlow<VetsUiState> = _uiState

    fun load(clinicId: Long) {
        viewModelScope.launch {
            _uiState.value = VetsUiState.Loading
            runCatching { api.getByClinicList(clinicId) }
                .onSuccess { list ->
                    _uiState.value =
                        VetsUiState.Success(
                            list.map {
                                Vet(
                                    userId = it.userId,
                                    firstName = it.firstName,
                                    lastName = it.lastName,
                                    specialization = it.specialization,
                                )
                            },
                        )
                }
                .onFailure { _uiState.value = VetsUiState.Error(it.message ?: "Load vets failed") }
        }
    }
}