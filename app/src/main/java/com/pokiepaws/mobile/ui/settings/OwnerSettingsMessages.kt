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
import com.pokiepaws.mobile.ui.auth.register.PasswordValidationError

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
