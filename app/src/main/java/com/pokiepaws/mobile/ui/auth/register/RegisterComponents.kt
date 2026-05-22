package com.pokiepaws.mobile.ui.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.validation.PasswordValidationError
import com.pokiepaws.mobile.domain.validation.PostalCodeValidationError
import com.pokiepaws.mobile.ui.settings.PasswordValidationErrors
import com.pokiepaws.mobile.util.theme.PokieWhite

@Composable
internal fun RegisterContent(
    state: RegisterUiState,
    isLoading: Boolean,
    errorMessage: String?,
    onEvent: (RegisterEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxBackground(modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(SPACING_LARGE.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.logo_content_description),
                modifier = Modifier.size(LOGO_SIZE.dp),
            )

            Spacer(modifier = Modifier.height(SPACING_MEDIUM.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CARD_ROUNDING.dp),
                colors = CardDefaults.cardColors(containerColor = PokieWhite),
                elevation = CardDefaults.cardElevation(CARD_ELEVATION.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.register_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.register_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(SPACING_MEDIUM.dp))

                    SectionLabel(stringResource(R.string.register_account_data))
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { onEvent(RegisterEvent.EmailChanged(it)) },
                        label = { Text(stringResource(R.string.email_label)) },
                        isError = state.emailValidationError != null,
                        supportingText = {
                            if (state.emailValidationError != null) {
                                Text(
                                    text = stringResource(R.string.register_email_invalid),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
                        label = { Text(stringResource(R.string.password_label)) },
                        visualTransformation =
                            if (state.passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        trailingIcon = {
                            IconButton(onClick = { onEvent(RegisterEvent.TogglePasswordVisibility) }) {
                                Icon(
                                    if (state.passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    null,
                                )
                            }
                        },
                        isError = state.passwordValidationErrors.isNotEmpty(),
                        supportingText = {
                            PasswordValidationMessage(errors = PasswordValidationErrors(state.passwordValidationErrors))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                    )

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                        label = { Text(stringResource(R.string.repeat_password_label)) },
                        isError = !state.passwordsMatch,
                        supportingText = {
                            if (!state.passwordsMatch) {
                                Text(
                                    stringResource(R.string.passwords_do_not_match),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        },
                        visualTransformation =
                            if (state.confirmPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        trailingIcon = {
                            IconButton(onClick = { onEvent(RegisterEvent.ToggleConfirmPasswordVisibility) }) {
                                Icon(
                                    if (state.confirmPasswordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    null,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SectionLabel(stringResource(R.string.register_personal_data))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = state.firstName,
                            onValueChange = { onEvent(RegisterEvent.FirstNameChanged(it)) },
                            label = { Text(stringResource(R.string.first_name_label)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                        OutlinedTextField(
                            value = state.lastName,
                            onValueChange = { onEvent(RegisterEvent.LastNameChanged(it)) },
                            label = { Text(stringResource(R.string.last_name_label)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    OutlinedTextField(
                        value = state.phoneNumber,
                        onValueChange = { onEvent(RegisterEvent.PhoneNumberChanged(it)) },
                        label = { Text(stringResource(R.string.phone_number_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        prefix = { Text("$POLAND_DIAL_CODE ") },
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SectionLabel(stringResource(R.string.register_address_data))
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.street,
                        onValueChange = { onEvent(RegisterEvent.StreetChanged(it)) },
                        label = { Text(stringResource(R.string.street_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                    )

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = state.houseNumber,
                            onValueChange = { onEvent(RegisterEvent.HouseNumberChanged(it)) },
                            label = { Text(stringResource(R.string.house_number_label)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                        OutlinedTextField(
                            value = state.apartmentNumber,
                            onValueChange = { onEvent(RegisterEvent.ApartmentNumberChanged(it)) },
                            label = { Text(stringResource(R.string.apartment_label)) },
                            placeholder = { Text(stringResource(R.string.optional_placeholder)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val postalCodeValidationError = state.postalCodeValidationError
                        OutlinedTextField(
                            value = state.postalCode,
                            onValueChange = { onEvent(RegisterEvent.PostalCodeChanged(it)) },
                            label = { Text(stringResource(R.string.postal_code_label)) },
                            modifier = Modifier.weight(WEIGHT_POSTAL_CODE),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                            isError = postalCodeValidationError != null,
                            supportingText = {
                                if (postalCodeValidationError != null) {
                                    Text(
                                        text = stringResource(postalCodeValidationError.messageRes),
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        )
                        OutlinedTextField(
                            value = state.city,
                            onValueChange = { onEvent(RegisterEvent.CityChanged(it)) },
                            label = { Text(stringResource(R.string.city_label)) },
                            modifier = Modifier.weight(WEIGHT_CITY),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = FONT_SIZE_ERROR.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(SPACING_MEDIUM.dp))

                    Button(
                        onClick = { onEvent(RegisterEvent.Submit) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(BUTTON_HEIGHT.dp),
                        shape = RoundedCornerShape(BUTTON_ROUNDING.dp),
                        enabled = !isLoading && state.canSubmitLocal,
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PokieWhite)
                        } else {
                            Text(
                                text = stringResource(R.string.register_button),
                                fontSize = FONT_SIZE_BUTTON.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    TextButton(
                        onClick = { onEvent(RegisterEvent.NavigateToLogin) },
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text(stringResource(R.string.register_login_prompt))
                    }
                }
            }
            Spacer(modifier = Modifier.height(SPACING_LARGE.dp))
        }
    }
}

@Composable
private fun PasswordValidationMessage(errors: PasswordValidationErrors) {
    if (errors.items.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        errors.items.forEach { error ->
            Text(
                text = stringResource(error.messageRes),
                color = MaterialTheme.colorScheme.error,
                fontSize = FONT_SIZE_ERROR.sp,
            )
        }
    }
}

private val PasswordValidationError.messageRes: Int
    get() =
        when (this) {
            PasswordValidationError.TooShort -> R.string.register_password_too_short
            PasswordValidationError.MissingUppercase -> R.string.register_password_missing_uppercase
            PasswordValidationError.MissingSpecialCharacter -> R.string.register_password_missing_special
            PasswordValidationError.ContainsUserName -> R.string.register_password_contains_user_name
            PasswordValidationError.ContainsSequence -> R.string.register_password_contains_sequence
            PasswordValidationError.ContainsRepeatedCharacters -> R.string.register_password_repeated_characters
            PasswordValidationError.TooCommon -> R.string.register_password_too_common
        }

private val PostalCodeValidationError.messageRes: Int
    get() =
        when (this) {
            PostalCodeValidationError.InvalidFormat -> R.string.postal_code_invalid_format
        }

@Composable
private fun BoxBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) { content() }
}

@Composable
private fun SectionLabel(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp)
        Text(
            text = "  ${title.uppercase()}  ",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary.copy(alpha = SECTION_DIVIDER_ALPHA),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp)
    }
}

private const val WEIGHT_POSTAL_CODE = 0.45f
private const val WEIGHT_CITY = 0.55f
private const val SECTION_DIVIDER_ALPHA = 0.7f
private const val CARD_ELEVATION = 4
private const val LOGO_SIZE = 140
private const val CARD_ROUNDING = 24
private const val INPUT_ROUNDING = 12
private const val BUTTON_ROUNDING = 14
private const val BUTTON_HEIGHT = 54
private const val SPACING_LARGE = 48
private const val SPACING_MEDIUM = 24
private const val SPACING_SMALL = 8
private const val FONT_SIZE_BUTTON = 16
private const val FONT_SIZE_ERROR = 12
