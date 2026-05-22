package com.pokiepaws.mobile.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.util.theme.PokieWhite

@Composable
internal fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = stringResource(R.string.logo_content_description),
        modifier = Modifier.size(350.dp),
    )
}

@Composable
internal fun LoginCard(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoginTitle()

            Spacer(modifier = Modifier.height(20.dp))

            EmailField(
                email = state.email,
                onEmailChange = { onEvent(LoginEvent.EmailChanged(it)) },
                isError = state.errorMessage != null,
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordField(
                password = state.password,
                onPasswordChange = { onEvent(LoginEvent.PasswordChanged(it)) },
                passwordVisible = state.passwordVisible,
                onTogglePasswordVisibility = { onEvent(LoginEvent.TogglePasswordVisibility) },
                isError = state.errorMessage != null,
            )

            ErrorText(message = state.errorMessage)

            Spacer(modifier = Modifier.height(12.dp))

            LoginButton(
                isLoading = state.isLoading,
                onClick = { onEvent(LoginEvent.SubmitLogin) },
            )

            Spacer(modifier = Modifier.height(8.dp))

            RegisterLink { onEvent(LoginEvent.RegisterClicked) }
        }
    }
}

@Composable
private fun LoginTitle() {
    Text(
        text = stringResource(R.string.login_title),
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    isError: Boolean,
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.email_label)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        isError = isError,
    )
}

@Composable
private fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isError: Boolean,
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.password_label)) },
        singleLine = true,
        visualTransformation =
            if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector =
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        isError = isError,
    )
}
@Composable
private fun ErrorText(message: String?) {
    if (message == null) return

    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = PokieWhite,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = stringResource(R.string.login_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun RegisterLink(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = stringResource(R.string.login_register_prompt),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
        )
    }
}
