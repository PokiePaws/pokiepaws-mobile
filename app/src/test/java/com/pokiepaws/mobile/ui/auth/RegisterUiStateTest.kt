package com.pokiepaws.mobile.ui.auth

import com.pokiepaws.mobile.ui.auth.register.EmailValidationError
import com.pokiepaws.mobile.ui.auth.register.PasswordValidationError
import com.pokiepaws.mobile.ui.auth.register.RegisterUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterUiStateTest {
    @Test
    fun invalidEmailIsRejected() {
        val state = RegisterUiState(email = "owner.pokiepaws")

        assertEquals(EmailValidationError.InvalidFormat, state.emailValidationError)
        assertFalse(state.canSubmitLocal)
    }

    @Test
    fun validEmailIsAccepted() {
        val state = RegisterUiState(email = "owner@pokiepaws.pl")

        assertNull(state.emailValidationError)
    }

    @Test
    fun weakPasswordIsRejected() {
        val state =
            RegisterUiState(
                email = "owner@pokiepaws.pl",
                password = "password",
                confirmPassword = "password",
            )

        assertTrue(PasswordValidationError.TooShort in state.passwordValidationErrors)
        assertTrue(PasswordValidationError.MissingUppercase in state.passwordValidationErrors)
        assertTrue(PasswordValidationError.MissingSpecialCharacter in state.passwordValidationErrors)
        assertTrue(PasswordValidationError.TooCommon in state.passwordValidationErrors)
        assertFalse(state.canSubmitLocal)
    }

    @Test
    fun passwordWithUserNamePartIsRejected() {
        val state =
            RegisterUiState(
                email = "gabriela@pokiepaws.pl",
                password = "Gabriela!Secure2026",
                confirmPassword = "Gabriela!Secure2026",
            )

        assertTrue(PasswordValidationError.ContainsUserName in state.passwordValidationErrors)
    }

    @Test
    fun passwordWithSequenceIsRejected() {
        val state =
            RegisterUiState(
                email = "owner@pokiepaws.pl",
                password = "Strongabc!2026",
                confirmPassword = "Strongabc!2026",
            )

        assertTrue(PasswordValidationError.ContainsSequence in state.passwordValidationErrors)
    }

    @Test
    fun passwordWithRepeatedCharactersIsRejected() {
        val state =
            RegisterUiState(
                email = "owner@pokiepaws.pl",
                password = "StrongBbb!2026",
                confirmPassword = "StrongBbb!2026",
            )

        assertTrue(PasswordValidationError.ContainsRepeatedCharacters in state.passwordValidationErrors)
    }

    @Test
    fun strongPasswordAllowsSubmit() {
        val state =
            RegisterUiState(
                email = "owner@pokiepaws.pl",
                password = "Safe.Delta!92",
                confirmPassword = "Safe.Delta!92",
            )

        assertTrue(state.passwordValidationErrors.isEmpty())
        assertTrue(state.canSubmitLocal)
    }
}
