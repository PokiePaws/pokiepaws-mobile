package com.pokiepaws.mobile.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieCream
import com.pokiepaws.mobile.ui.theme.PokieWhite

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 48
private const val CONTENT_OFFSET = -16
private const val CARD_ROUNDING = 24
private const val ICON_BG_ROUNDING = 12
private const val INDICATOR_SIZE = 18

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OwnerSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        OwnerSettingsHeader(onBack = onBack)
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .offset(y = CONTENT_OFFSET.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
        ) {
            PhoneSection(state = state, onEvent = viewModel::onEvent)
            Spacer(modifier = Modifier.height(16.dp))
            AddressSection(state = state, onEvent = viewModel::onEvent)
            Spacer(modifier = Modifier.height(16.dp))
            TravelSection(state = state, onEvent = viewModel::onEvent)
            Spacer(modifier = Modifier.height(16.dp))
            PasswordSection(state = state, onEvent = viewModel::onEvent)
            ErrorText(state.errorMessage)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OwnerSettingsHeader(onBack: () -> Unit) {
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
                .padding(top = HEADER_TOP_PADDING.dp, bottom = HEADER_BOTTOM_PADDING.dp)
                .padding(horizontal = 8.dp),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.settings_back),
                tint = PokieWhite,
            )
        }
        Text(
            text = stringResource(R.string.owner_settings_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PokieWhite,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun PhoneSection(
    state: OwnerSettingsUiState,
    onEvent: (OwnerSettingsEvent) -> Unit,
) {
    SettingsSectionCard(
        icon = Icons.Default.Phone,
        titleRes = R.string.owner_settings_phone_title,
    ) {
        SettingsTextField(
            value = state.phoneNumber,
            labelRes = R.string.phone_number_label,
            keyboardType = KeyboardType.Phone,
            onValueChange = { onEvent(OwnerSettingsEvent.PhoneNumberChanged(it)) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        SaveButton(
            textRes = R.string.owner_settings_save_phone,
            enabled = state.canSavePhone && !state.isSavingPhone,
            loading = state.isSavingPhone,
            onClick = { onEvent(OwnerSettingsEvent.SavePhone) },
        )
        StatusText(
            visible = state.phoneSaved,
            textRes = R.string.owner_settings_phone_saved,
            color = PokieBlueDark,
        )
    }
}

@Composable
private fun AddressSection(
    state: OwnerSettingsUiState,
    onEvent: (OwnerSettingsEvent) -> Unit,
) {
    SettingsSectionCard(
        icon = Icons.Default.Home,
        titleRes = R.string.owner_settings_address_title,
    ) {
        SettingsTextField(
            value = state.street,
            labelRes = R.string.owner_settings_street,
            onValueChange = { onEvent(OwnerSettingsEvent.StreetChanged(it)) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsTextField(
                value = state.houseNumber,
                labelRes = R.string.owner_settings_house_number,
                modifier = Modifier.weight(1f),
                onValueChange = { onEvent(OwnerSettingsEvent.HouseNumberChanged(it)) },
            )
            SettingsTextField(
                value = state.apartmentNumber,
                labelRes = R.string.owner_settings_apartment_number,
                modifier = Modifier.weight(1f),
                onValueChange = { onEvent(OwnerSettingsEvent.ApartmentNumberChanged(it)) },
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SettingsTextField(
            value = state.city,
            labelRes = R.string.owner_settings_city,
            onValueChange = { onEvent(OwnerSettingsEvent.CityChanged(it)) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsTextField(
                value = state.postalCode,
                labelRes = R.string.owner_settings_postal_code,
                modifier = Modifier.weight(1f),
                onValueChange = { onEvent(OwnerSettingsEvent.PostalCodeChanged(it)) },
            )
            SettingsTextField(
                value = state.country,
                labelRes = R.string.owner_settings_country,
                modifier = Modifier.weight(1f),
                onValueChange = { onEvent(OwnerSettingsEvent.CountryChanged(it)) },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        SaveButton(
            textRes = R.string.owner_settings_save_address,
            enabled = state.canSaveAddress && !state.isSavingAddress,
            loading = state.isSavingAddress,
            onClick = { onEvent(OwnerSettingsEvent.SaveAddress) },
        )
        StatusText(
            visible = state.addressSaved,
            textRes = R.string.owner_settings_address_saved,
            color = PokieBlueDark,
        )
    }
}

@Composable
private fun TravelSection(
    state: OwnerSettingsUiState,
    onEvent: (OwnerSettingsEvent) -> Unit,
) {
    SettingsSectionCard(
        icon = Icons.Default.FlightTakeoff,
        titleRes = R.string.owner_settings_travel_title,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.owner_settings_foreign_travel_planned),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.owner_settings_foreign_travel_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = state.foreignTravelPlanned,
                onCheckedChange = { onEvent(OwnerSettingsEvent.ForeignTravelPlannedChanged(it)) },
            )
        }
    }
}

@Composable
private fun PasswordSection(
    state: OwnerSettingsUiState,
    onEvent: (OwnerSettingsEvent) -> Unit,
) {
    SettingsSectionCard(
        icon = Icons.Default.Lock,
        titleRes = R.string.owner_settings_password_title,
    ) {
        PasswordField(
            value = state.currentPassword,
            labelRes = R.string.owner_settings_current_password,
            visible = state.currentPasswordVisible,
            onToggleVisibility = { onEvent(OwnerSettingsEvent.ToggleCurrentPasswordVisibility) },
            onValueChange = { onEvent(OwnerSettingsEvent.CurrentPasswordChanged(it)) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = state.newPassword,
            labelRes = R.string.owner_settings_new_password,
            visible = state.newPasswordVisible,
            onToggleVisibility = { onEvent(OwnerSettingsEvent.ToggleNewPasswordVisibility) },
            onValueChange = { onEvent(OwnerSettingsEvent.NewPasswordChanged(it)) },
        )
        PasswordValidationMessages(state.passwordValidationErrors)
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = state.confirmNewPassword,
            labelRes = R.string.owner_settings_confirm_new_password,
            visible = state.confirmNewPasswordVisible,
            isError = !state.passwordsMatch,
            onToggleVisibility = { onEvent(OwnerSettingsEvent.ToggleConfirmNewPasswordVisibility) },
            onValueChange = { onEvent(OwnerSettingsEvent.ConfirmNewPasswordChanged(it)) },
        )
        StatusText(
            visible = !state.passwordsMatch,
            textRes = R.string.passwords_do_not_match,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SaveButton(
            textRes = R.string.owner_settings_change_password,
            enabled = state.canChangePassword && !state.isChangingPassword,
            loading = state.isChangingPassword,
            onClick = { onEvent(OwnerSettingsEvent.ChangePassword) },
        )
        StatusText(
            visible = state.passwordChanged,
            textRes = R.string.owner_settings_password_changed,
            color = PokieBlueDark,
        )
    }
}

@Composable
private fun SettingsSectionCard(
    icon: ImageVector,
    @StringRes titleRes: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CARD_ROUNDING.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(ICON_BG_ROUNDING.dp))
                            .background(PokieCream),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PokieBlue,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(titleRes),
                    fontWeight = FontWeight.Bold,
                    color = PokieBlueDark,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun SettingsTextField(
    value: String,
    @StringRes labelRes: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier =
            modifier
                .fillMaxWidth(),
    )
}

@Composable
private fun PasswordField(
    value: String,
    @StringRes labelRes: Int,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        singleLine = true,
        isError = isError,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                )
            }
        },
        modifier =
            modifier
                .fillMaxWidth(),
    )
}

@Composable
private fun SaveButton(
    @StringRes textRes: Int,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(INDICATOR_SIZE.dp),
                strokeWidth = 2.dp,
                color = PokieWhite,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = stringResource(textRes))
    }
}

@Composable
private fun StatusText(
    visible: Boolean,
    @StringRes textRes: Int,
    color: Color,
) {
    if (visible) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(textRes),
                color = color,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
