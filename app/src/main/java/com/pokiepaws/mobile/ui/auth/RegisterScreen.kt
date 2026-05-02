package com.pokiepaws.mobile.ui.auth

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.ui.theme.PokieWhite
import com.pokiepaws.mobile.util.Country
import com.pokiepaws.mobile.util.popularCountries

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
private const val FONT_SIZE_FLAG = 18

@Composable
fun RegisterScreen(
    onNavigateToVerification: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }

    var phoneCountry by remember { mutableStateOf(popularCountries.first()) }
    var residenceCountry by remember {
        mutableStateOf(popularCountries.find { it.name == "Polska" } ?: popularCountries.first())
    }

    var street by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var apartmentNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    val passwordsMatch = password == confirmPassword || confirmPassword.isEmpty()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.RegisterSuccess) {
            onNavigateToVerification(email)
            viewModel.resetState()
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(SPACING_LARGE.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "PokiePaws Logo",
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
                        text = "Utwórz konto",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Wypełnij dane, aby dołączyć do nas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(SPACING_MEDIUM.dp))

                    SectionLabel("Dane konta")
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Hasło") },
                        visualTransformation =
                            if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) {
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

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Powtórz hasło") },
                        isError = !passwordsMatch,
                        supportingText = {
                            if (!passwordsMatch) {
                                Text("Hasła nie są identyczne", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        visualTransformation =
                            if (confirmPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) {
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

                    SectionLabel("Dane osobowe")
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("Imię") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Nazwisko") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Numer telefonu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        prefix = { Text("${phoneCountry.dialCode} ") },
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SectionLabel("Adres zamieszkania")
                    Spacer(modifier = Modifier.height(12.dp))

                    CountryDropdownField(
                        selectedCountry = residenceCountry,
                        onCountrySelected = { residenceCountry = it },
                    )

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("Ulica") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                    )

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = houseNumber,
                            onValueChange = { houseNumber = it },
                            label = { Text("Nr domu") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                        OutlinedTextField(
                            value = apartmentNumber,
                            onValueChange = { apartmentNumber = it },
                            label = { Text("Lokal") },
                            placeholder = { Text("opcjonalnie") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(SPACING_SMALL.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = postalCode,
                            onValueChange = { postalCode = it },
                            label = { Text("Kod") },
                            modifier = Modifier.weight(WEIGHT_POSTAL_CODE),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Miasto") },
                            modifier = Modifier.weight(WEIGHT_CITY),
                            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
                        )
                    }

                    if (uiState is AuthUiState.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = FONT_SIZE_ERROR.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(SPACING_MEDIUM.dp))

                    Button(
                        onClick = {
                            viewModel.register(
                                email = email,
                                password = password,
                                firstName = firstName,
                                lastName = lastName,
                                phoneNumber = "${phoneCountry.dialCode}$phoneNumber",
                                street = street,
                                houseNumber = houseNumber,
                                apartmentNumber = apartmentNumber.ifBlank { null },
                                city = city,
                                postalCode = postalCode,
                                country = residenceCountry.name,
                            )
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(BUTTON_HEIGHT.dp),
                        shape = RoundedCornerShape(BUTTON_ROUNDING.dp),
                        enabled = (uiState !is AuthUiState.Loading) && passwordsMatch && email.isNotEmpty(),
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PokieWhite)
                        } else {
                            Text("Zarejestruj się", fontSize = FONT_SIZE_BUTTON.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text("Masz już konto? Zaloguj się")
                    }
                }
            }
            Spacer(modifier = Modifier.height(SPACING_LARGE.dp))
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdownField(
    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = "${selectedCountry.flag} ${selectedCountry.name}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Kraj zamieszkania") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier =
                Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
            shape = RoundedCornerShape(INPUT_ROUNDING.dp),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            popularCountries.forEach { country ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(country.flag, fontSize = FONT_SIZE_FLAG.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(country.name)
                        }
                    },
                    onClick = {
                        onCountrySelected(country)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
