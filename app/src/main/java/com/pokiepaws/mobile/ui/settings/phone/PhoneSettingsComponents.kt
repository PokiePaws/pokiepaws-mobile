package com.pokiepaws.mobile.ui.settings.phone

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.ui.settings.OwnerSettingsEvent
import com.pokiepaws.mobile.ui.settings.OwnerSettingsUiState
import com.pokiepaws.mobile.ui.settings.SaveButton
import com.pokiepaws.mobile.ui.settings.SettingsSectionCard
import com.pokiepaws.mobile.ui.settings.SettingsTextField
import com.pokiepaws.mobile.ui.settings.StatusText
import com.pokiepaws.mobile.util.theme.PokieBlueDark

@Composable
internal fun PhoneSettingsSection(
    state: OwnerSettingsUiState,
    onEvent: (OwnerSettingsEvent) -> Unit,
) {
    val sectionState = PhoneSettingsUiState.from(state)

    SettingsSectionCard(
        icon = Icons.Default.Phone,
        titleRes = R.string.owner_settings_phone_title,
    ) {
        SettingsTextField(
            value = sectionState.phoneNumber,
            labelRes = R.string.phone_number_label,
            keyboardType = KeyboardType.Phone,
            onValueChange = { onEvent(OwnerSettingsEvent.PhoneNumberChanged(it)) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        SaveButton(
            textRes = R.string.owner_settings_save_phone,
            enabled = sectionState.canSave && !sectionState.isSaving,
            loading = sectionState.isSaving,
            onClick = { onEvent(OwnerSettingsEvent.SavePhone) },
        )
        StatusText(
            visible = sectionState.saved,
            textRes = R.string.owner_settings_phone_saved,
            color = PokieBlueDark,
        )
    }
}
