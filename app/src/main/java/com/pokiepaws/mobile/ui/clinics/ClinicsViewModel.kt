package com.pokiepaws.mobile.ui.clinics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokiepaws.mobile.data.remote.service.ClinicApiService
import com.pokiepaws.mobile.domain.model.Clinic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClinicsViewModel
    @Inject
    constructor(
        private val api: ClinicApiService,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<ClinicsUiState>(ClinicsUiState.Loading)
        val uiState: StateFlow<ClinicsUiState> = _uiState

        fun load() {
            viewModelScope.launch {
                _uiState.value = ClinicsUiState.Loading
                runCatching { api.getAll() }
                    .onSuccess { list ->
                        _uiState.value =
                            ClinicsUiState.Success(
                                list.map {
                                    Clinic(
                                        id = it.id,
                                        clinicName = it.clinicName,
                                        city = it.city,
                                        street = it.street,
                                        houseNumber = it.houseNumber,
                                        postalCode = it.postalCode,
                                        country = it.country,
                                        phone = it.phone,
                                        email = it.email,
                                    )
                                },
                            )
                    }
                    .onFailure { _uiState.value = ClinicsUiState.Error(it.message ?: "Load clinics failed") }
            }
        }
    }
