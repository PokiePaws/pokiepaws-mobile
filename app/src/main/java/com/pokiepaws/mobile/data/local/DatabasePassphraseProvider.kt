package com.pokiepaws.mobile.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabasePassphraseProvider
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        fun getPassphrase(): ByteArray {
            val encrypted = preferences.getString(PASSPHRASE_KEY, null)
            val passphrase =
                encrypted?.decrypt()
                    ?: generatePassphrase().also { generated ->
                        preferences.edit {
                            putString(PASSPHRASE_KEY, generated.encrypt())
                        }
                    }
            return passphrase
        }

        private fun generatePassphrase(): ByteArray = ByteArray(PASSPHRASE_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }

        private fun ByteArray.encrypt(): String {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            return "${cipher.iv.toBase64()}:${cipher.doFinal(this).toBase64()}"
        }

        private fun String.decrypt(): ByteArray? =
            runCatching {
                val parts = split(":")
                if (parts.size != ENCRYPTED_PARTS_COUNT) return@runCatching null

                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(
                    Cipher.DECRYPT_MODE,
                    getOrCreateSecretKey(),
                    GCMParameterSpec(GCM_TAG_LENGTH_BITS, parts[0].fromBase64()),
                )
                cipher.doFinal(parts[1].fromBase64())
            }.getOrNull()

        private fun getOrCreateSecretKey(): SecretKey {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { entry ->
                return entry.secretKey
            }

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            keyGenerator.init(
                KeyGenParameterSpec
                    .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .setKeySize(KEY_SIZE_BITS)
                    .build(),
            )
            return keyGenerator.generateKey()
        }

        private val preferences by lazy {
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

        private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

        private fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

        private companion object {
            const val ANDROID_KEYSTORE = "AndroidKeyStore"
            const val KEY_ALIAS = "pokiepaws_database_passphrase"
            const val TRANSFORMATION = "AES/GCM/NoPadding"
            const val KEY_SIZE_BITS = 256
            const val GCM_TAG_LENGTH_BITS = 128
            const val ENCRYPTED_PARTS_COUNT = 2
            const val PASSPHRASE_LENGTH_BYTES = 32
            const val PREFERENCES_NAME = "pokiepaws_secure_database"
            const val PASSPHRASE_KEY = "database_passphrase"
        }
    }
