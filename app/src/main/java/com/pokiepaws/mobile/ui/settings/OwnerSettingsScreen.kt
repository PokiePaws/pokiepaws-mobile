package com.pokiepaws.mobile.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.pokiepaws.mobile.ui.settings.address.AddressSettingsSection
import com.pokiepaws.mobile.ui.settings.password.PasswordSettingsSection
import com.pokiepaws.mobile.ui.settings.phone.PhoneSettingsSection
import com.pokiepaws.mobile.util.theme.PokieBlue
import com.pokiepaws.mobile.util.theme.PokieCream
import com.pokiepaws.mobile.util.theme.PokieWhite

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OwnerSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }

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
            PhoneSettingsSection(state = state, onEvent = viewModel::onEvent)
            Spacer(modifier = Modifier.height(16.dp))
            AddressSettingsSection(state = state, onEvent = viewModel::onEvent)
            Spacer(modifier = Modifier.height(16.dp))
            TravelSection(state = state, onEvent = viewModel::onEvent)
            Spacer(modifier = Modifier.height(16.dp))
            PasswordSettingsSection(state = state, onEvent = viewModel::onEvent)
            Spacer(modifier = Modifier.height(16.dp))
            AccountSection(
                state = state,
                onDeleteAccountClick = { showDeleteAccountDialog = true },
            )
            ErrorText(state.errorMessage)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            isDeleting = state.isDeletingAccount,
            onConfirm = {
                showDeleteAccountDialog = false
                viewModel.deleteAccount(onAccountDeleted)
            },
            onDismiss = {
                if (!state.isDeletingAccount) {
                    showDeleteAccountDialog = false
                    viewModel.onEvent(OwnerSettingsEvent.ClearDeleteAccountError)
                }
            },
        )
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
private fun AccountSection(
    state: OwnerSettingsUiState,
    onDeleteAccountClick: () -> Unit,
) {
    SettingsSectionCard(
        icon = Icons.Default.DeleteOutline,
        titleRes = R.string.owner_settings_account_title,
    ) {
        DeleteAccountButton(
            isDeleting = state.isDeletingAccount,
            onClick = onDeleteAccountClick,
        )

        if (state.deleteAccountError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.deleteAccountError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun DeleteAccountButton(
    isDeleting: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = !isDeleting,
                    onClick = onClick,
                ),
        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
        colors = CardDefaults.cardColors(containerColor = DeleteIconBg),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(ICON_BG_ROUNDING.dp))
                        .background(PokieWhite),
                contentAlignment = Alignment.Center,
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = DeleteColor,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = DeleteColor,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text =
                    if (isDeleting) {
                        stringResource(R.string.profile_delete_account_progress)
                    } else {
                        stringResource(R.string.profile_delete_account)
                    },
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = DeleteColor,
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = DeleteColor.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun DeleteAccountDialog(
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.profile_delete_account_dialog_title)) },
        text = { Text(text = stringResource(R.string.profile_delete_account_dialog_message)) },
        confirmButton = {
            TextButton(
                enabled = !isDeleting,
                onClick = onConfirm,
            ) {
                Text(
                    text = stringResource(R.string.profile_delete_account_confirm),
                    color = DeleteColor,
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isDeleting,
                onClick = onDismiss,
            ) {
                Text(text = stringResource(R.string.profile_delete_account_cancel))
            }
        },
    )
}

@Composable
internal fun SettingsSectionCard(
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(ICON_BG_ROUNDING.dp))
                                .background(PokieCream),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = PokieBlue,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(titleRes).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = SECTION_DIVIDER_ALPHA),
                    )
                }
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
internal fun SettingsTextField(
    value: String,
    @StringRes labelRes: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    @StringRes supportingTextRes: Int? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        singleLine = true,
        isError = isError,
        supportingText = {
            if (supportingTextRes != null) {
                Text(
                    text = stringResource(supportingTextRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
internal fun PasswordField(
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
        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
internal fun SaveButton(
    @StringRes textRes: Int,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(50.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(INDICATOR_SIZE.dp),
                strokeWidth = 2.dp,
                color = PokieWhite,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = stringResource(textRes),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
internal fun StatusText(
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

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 48
private const val CONTENT_OFFSET = -16
private const val CARD_ROUNDING = 24
private const val ICON_BG_ROUNDING = 12
private const val INDICATOR_SIZE = 18
private const val INPUT_ROUNDING = 12
private const val SECTION_DIVIDER_ALPHA = 0.7f
private val DeleteColor = Color(0xFFEF4444)
private val DeleteIconBg = Color(0xFFFEE2E2)
