package com.pokiepaws.mobile.ui.settings.phone

import com.pokiepaws.mobile.ui.settings.OwnerSettingsUiState

data class PhoneSettingsUiState(
    val phoneNumber: String,
    val isSaving: Boolean,
    val saved: Boolean,
    val canSave: Boolean,
) {
    companion object {
        fun from(state: OwnerSettingsUiState): PhoneSettingsUiState =
            PhoneSettingsUiState(
                phoneNumber = state.phoneNumber,
                isSaving = state.isSavingPhone,
                saved = state.phoneSaved,
                canSave = state.canSavePhone,
            )
    }
}
