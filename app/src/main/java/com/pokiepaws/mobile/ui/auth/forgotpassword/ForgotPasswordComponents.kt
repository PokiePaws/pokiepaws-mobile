package com.pokiepaws.mobile.ui.auth.forgotpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.util.theme.PokieBlue

@Composable
internal fun ForgotPasswordContent(
    state: ForgotPasswordUiState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    modifier: Modifier = Modifier,
    serverFormatMessage: String,
) {
    val errorMessageForUi = errorMessageForUi(state.errorMessage, serverFormatMessage)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(PokieBlue),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            ForgotPasswordHeader()

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 20.dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ForgotPasswordTitleAndDescription()

                    Spacer(modifier = Modifier.height(32.dp))

                    EmailField(
                        email = state.email,
                        onEmailChange = { onEvent(ForgotPasswordEvent.EmailChanged(it)) },
                        isError = errorMessageForUi != null,
                    )

                    ErrorText(errorMessageForUi)

                    Spacer(modifier = Modifier.height(32.dp))

                    SendLinkButton(
                        isLoading = state.isLoading,
                        onClick = { onEvent(ForgotPasswordEvent.Submit) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { onEvent(ForgotPasswordEvent.NavigateBack) }) {
                        Text(stringResource(R.string.back_to_login), color = PokieBlue)
                    }
                }
            }
        }
    }
}

private fun errorMessageForUi(
    errorMessage: String?,
    serverFormatMessage: String,
): String? {
    return when {
        errorMessage.isNullOrBlank() -> null
        errorMessage.contains("Expected quotation mark") -> serverFormatMessage
        else -> errorMessage
    }
}

@Composable
private fun ForgotPasswordHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(R.string.logo_content_description),
            modifier = Modifier.size(180.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ForgotPasswordTitleAndDescription() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.forgot_password_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PokieBlue,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.forgot_password_description),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp,
        )
    }
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
        label = { Text(stringResource(R.string.email_address_label)) },
        placeholder = { Text(stringResource(R.string.email_example_placeholder)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PokieBlue,
                focusedLabelColor = PokieBlue,
            ),
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
        modifier =
            Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun SendLinkButton(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PokieBlue),
        enabled = !isLoading,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
            )
        } else {
            Text(
                text = stringResource(R.string.forgot_password_send_link),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
