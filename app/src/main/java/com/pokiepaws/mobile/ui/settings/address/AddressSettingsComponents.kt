package com.pokiepaws.mobile.ui.settings.address

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
internal fun AddressSettingsSection(
    state: OwnerSettingsUiState,
    onEvent: (OwnerSettingsEvent) -> Unit,
) {
    val sectionState = AddressSettingsUiState.from(state)

    SettingsSectionCard(
        icon = Icons.Default.Home,
        titleRes = R.string.owner_settings_address_title,
    ) {
        SettingsTextField(
            value = sectionState.street,
            labelRes = R.string.owner_settings_street,
            onValueChange = { onEvent(OwnerSettingsEvent.StreetChanged(it)) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsTextField(
                value = sectionState.houseNumber,
                labelRes = R.string.owner_settings_house_number,
                modifier = Modifier.weight(1f),
                onValueChange = { onEvent(OwnerSettingsEvent.HouseNumberChanged(it)) },
            )
            SettingsTextField(
                value = sectionState.apartmentNumber,
                labelRes = R.string.owner_settings_apartment_number,
                modifier = Modifier.weight(1f),
                onValueChange = { onEvent(OwnerSettingsEvent.ApartmentNumberChanged(it)) },
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SettingsTextField(
            value = sectionState.city,
            labelRes = R.string.owner_settings_city,
            onValueChange = { onEvent(OwnerSettingsEvent.CityChanged(it)) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsTextField(
                value = sectionState.postalCode,
                labelRes = R.string.owner_settings_postal_code,
                modifier = Modifier.weight(1f),
                onValueChange = { onEvent(OwnerSettingsEvent.PostalCodeChanged(it)) },
            )
            SettingsTextField(
                value = sectionState.country,
                labelRes = R.string.owner_settings_country,
                modifier = Modifier.weight(1f),
                onValueChange = { onEvent(OwnerSettingsEvent.CountryChanged(it)) },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        SaveButton(
            textRes = R.string.owner_settings_save_address,
            enabled = sectionState.canSave && !sectionState.isSaving,
            loading = sectionState.isSaving,
            onClick = { onEvent(OwnerSettingsEvent.SaveAddress) },
        )
        StatusText(
            visible = sectionState.saved,
            textRes = R.string.owner_settings_address_saved,
            color = PokieBlueDark,
        )
    }
}
