package com.pokiepaws.mobile.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.validation.PasswordValidationError

@androidx.annotation.StringRes
fun passwordValidationMessageRes(error: PasswordValidationError): Int =
    when (error) {
        PasswordValidationError.TooShort -> R.string.register_password_too_short
        PasswordValidationError.MissingUppercase -> R.string.register_password_missing_uppercase
        PasswordValidationError.MissingSpecialCharacter -> R.string.register_password_missing_special
        PasswordValidationError.ContainsUserName -> R.string.register_password_contains_user_name
        PasswordValidationError.ContainsSequence -> R.string.register_password_contains_sequence
        PasswordValidationError.ContainsRepeatedCharacters -> R.string.register_password_repeated_characters
        PasswordValidationError.TooCommon -> R.string.register_password_too_common
    }

@Composable
internal fun PasswordValidationMessages(
    errors: List<PasswordValidationError>,
    modifier: Modifier = Modifier,
) {
    if (errors.isNotEmpty()) {
        Column(modifier = modifier.padding(top = 8.dp)) {
            errors.forEach { error ->
                Text(
                    text = stringResource(passwordValidationMessageRes(error)),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
internal fun ErrorText(
    message: String?,
    modifier: Modifier = Modifier,
) {
    if (!message.isNullOrBlank()) {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
