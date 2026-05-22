package com.pokiepaws.mobile.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_token_prefs")

@Singleton
class TokenManager
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        private val accessTokenKey = stringPreferencesKey("access_token_encrypted")
        private val refreshTokenKey = stringPreferencesKey("refresh_token_encrypted")
        private val legacyTokenKey = stringPreferencesKey("jwt_token")

        val token: Flow<String?> =
            context.dataStore.data.map { preferences ->
                preferences[accessTokenKey]?.decryptToken()
                    ?: preferences[legacyTokenKey]
            }

        val refreshToken: Flow<String?> =
            context.dataStore.data.map { preferences ->
                preferences[refreshTokenKey]?.decryptToken()
            }

        suspend fun saveTokens(
            accessToken: String,
            refreshToken: String?,
        ) {
            context.dataStore.edit { preferences ->
                preferences[accessTokenKey] = accessToken.encryptToken()
                refreshToken?.let { preferences[refreshTokenKey] = it.encryptToken() }
                preferences.remove(legacyTokenKey)
            }
        }

        suspend fun clearToken() {
            context.dataStore.edit { preferences ->
                preferences.remove(accessTokenKey)
                preferences.remove(refreshTokenKey)
                preferences.remove(legacyTokenKey)
            }
        }

        private fun String.encryptToken(): String {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val encrypted = cipher.doFinal(toByteArray(Charsets.UTF_8))
            return "${cipher.iv.toBase64()}:${encrypted.toBase64()}"
        }

        private fun String.decryptToken(): String? =
            runCatching {
                val parts = split(":")
                if (parts.size != ENCRYPTED_PARTS_COUNT) return@runCatching null

                val iv = parts[0].fromBase64()
                val encrypted = parts[1].fromBase64()
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
                cipher.doFinal(encrypted).toString(Charsets.UTF_8)
            }.getOrNull()

        private fun getOrCreateSecretKey(): SecretKey {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let {
                return it.secretKey
            }

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keySpec =
                KeyGenParameterSpec
                    .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            keyGenerator.init(keySpec)
            return keyGenerator.generateKey()
        }

        private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

        private fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

        private companion object {
            const val ANDROID_KEYSTORE = "AndroidKeyStore"
            const val KEY_ALIAS = "pokiepaws_auth_tokens"
            const val TRANSFORMATION = "AES/GCM/NoPadding"
            const val GCM_TAG_LENGTH_BITS = 128
            const val ENCRYPTED_PARTS_COUNT = 2
        }
    }
