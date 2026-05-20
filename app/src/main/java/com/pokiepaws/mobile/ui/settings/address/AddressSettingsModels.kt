package com.pokiepaws.mobile.ui.settings.address

import com.pokiepaws.mobile.domain.validation.PostalCodeValidationError
import com.pokiepaws.mobile.ui.settings.OwnerSettingsUiState

data class AddressSettingsUiState(
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String,
    val city: String,
    val postalCode: String,
    val postalCodeValidationError: PostalCodeValidationError?,
    val country: String,
    val isSaving: Boolean,
    val saved: Boolean,
    val canSave: Boolean,
) {
    companion object {
        fun from(state: OwnerSettingsUiState): AddressSettingsUiState =
            AddressSettingsUiState(
                street = state.street,
                houseNumber = state.houseNumber,
                apartmentNumber = state.apartmentNumber,
                city = state.city,
                postalCode = state.postalCode,
                postalCodeValidationError = state.postalCodeValidationError,
                country = state.country,
                isSaving = state.isSavingAddress,
                saved = state.addressSaved,
                canSave = state.canSaveAddress,
            )
    }
}
