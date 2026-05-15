package com.pokiepaws.mobile.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.domain.model.RegistrationDraft

@Composable
fun RegisterScreen(
    onNavigateToVerification: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var state by remember { mutableStateOf(RegisterUiState()) }

    val isLoading = uiState is AuthUiState.Loading
    val errorMessage = (uiState as? AuthUiState.Error)?.message

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.RegisterSuccess) {
            onNavigateToVerification(state.email)
            viewModel.resetState()
        }
    }

    RegisterContent(
        state = state,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onEvent = { event ->
            handleRegisterEvent(
                event = event,
                state = state,
                updateState = { state = it },
                onSubmit = viewModel::register,
                onNavigateToLogin = onNavigateToLogin,
            )
        },
        modifier = modifier,
    )
}

@Suppress("CyclomaticComplexMethod")
private fun handleRegisterEvent(
    event: RegisterEvent,
    state: RegisterUiState,
    updateState: (RegisterUiState) -> Unit,
    onSubmit: (RegistrationDraft) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    when (event) {
        is RegisterEvent.EmailChanged -> updateState(state.copy(email = event.value))
        is RegisterEvent.PasswordChanged -> updateState(state.copy(password = event.value))
        is RegisterEvent.ConfirmPasswordChanged -> updateState(state.copy(confirmPassword = event.value))
        is RegisterEvent.FirstNameChanged -> updateState(state.copy(firstName = event.value))
        is RegisterEvent.LastNameChanged -> updateState(state.copy(lastName = event.value))
        RegisterEvent.TogglePasswordVisibility -> updateState(state.copy(passwordVisible = !state.passwordVisible))
        RegisterEvent.ToggleConfirmPasswordVisibility ->
            updateState(state.copy(confirmPasswordVisible = !state.confirmPasswordVisible))

        is RegisterEvent.PhoneNumberChanged -> updateState(state.copy(phoneNumber = event.value))
        is RegisterEvent.PhoneCountryChanged -> updateState(state.copy(phoneCountry = event.value))
        is RegisterEvent.ResidenceCountryChanged -> updateState(state.copy(residenceCountry = event.value))
        is RegisterEvent.StreetChanged -> updateState(state.copy(street = event.value))
        is RegisterEvent.HouseNumberChanged -> updateState(state.copy(houseNumber = event.value))
        is RegisterEvent.ApartmentNumberChanged -> updateState(state.copy(apartmentNumber = event.value))
        is RegisterEvent.CityChanged -> updateState(state.copy(city = event.value))
        is RegisterEvent.PostalCodeChanged -> updateState(state.copy(postalCode = event.value))
        RegisterEvent.Submit -> onSubmit(state.toRegistrationDraft())
        RegisterEvent.NavigateToLogin -> onNavigateToLogin()
    }
}
