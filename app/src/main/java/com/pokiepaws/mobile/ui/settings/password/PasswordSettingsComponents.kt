package com.pokiepaws.mobile.ui.settings.password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.ui.settings.OwnerSettingsEvent
import com.pokiepaws.mobile.ui.settings.OwnerSettingsUiState
import com.pokiepaws.mobile.ui.settings.PasswordField
import com.pokiepaws.mobile.ui.settings.PasswordValidationMessages
import com.pokiepaws.mobile.ui.settings.SaveButton
import com.pokiepaws.mobile.ui.settings.SettingsSectionCard
import com.pokiepaws.mobile.ui.settings.StatusText
import com.pokiepaws.mobile.util.theme.PokieBlueDark

@Composable
internal fun PasswordSettingsSection(
    state: OwnerSettingsUiState,
    onEvent: (OwnerSettingsEvent) -> Unit,
) {
    val sectionState = PasswordSettingsUiState.from(state)

    SettingsSectionCard(
        icon = Icons.Default.Lock,
        titleRes = R.string.owner_settings_password_title,
    ) {
        PasswordField(
            value = sectionState.currentPassword,
            labelRes = R.string.owner_settings_current_password,
            visible = sectionState.currentPasswordVisible,
            onToggleVisibility = { onEvent(OwnerSettingsEvent.ToggleCurrentPasswordVisibility) },
            onValueChange = { onEvent(OwnerSettingsEvent.CurrentPasswordChanged(it)) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = sectionState.newPassword,
            labelRes = R.string.owner_settings_new_password,
            visible = sectionState.newPasswordVisible,
            onToggleVisibility = { onEvent(OwnerSettingsEvent.ToggleNewPasswordVisibility) },
            onValueChange = { onEvent(OwnerSettingsEvent.NewPasswordChanged(it)) },
        )
        PasswordValidationMessages(sectionState.validationErrors)
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = sectionState.confirmNewPassword,
            labelRes = R.string.owner_settings_confirm_new_password,
            visible = sectionState.confirmNewPasswordVisible,
            isError = !sectionState.passwordsMatch,
            onToggleVisibility = { onEvent(OwnerSettingsEvent.ToggleConfirmNewPasswordVisibility) },
            onValueChange = { onEvent(OwnerSettingsEvent.ConfirmNewPasswordChanged(it)) },
        )
        StatusText(
            visible = !sectionState.passwordsMatch,
            textRes = R.string.passwords_do_not_match,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SaveButton(
            textRes = R.string.owner_settings_change_password,
            enabled = sectionState.canChange && !sectionState.isChanging,
            loading = sectionState.isChanging,
            onClick = { onEvent(OwnerSettingsEvent.ChangePassword) },
        )
        StatusText(
            visible = sectionState.changed,
            textRes = R.string.owner_settings_password_changed,
            color = PokieBlueDark,
        )
    }
}
