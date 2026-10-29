package com.example.security

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    // Master seed for encrypting configs and app features (completely anonymized)
    private const val MASTER_SECRET = "iZoneMax_2026_Vault_Secret_Core_AES256_GCM"
    private const val SALT = "iZone_Morocco_Salt_#99"

    // Irreversible salted SHA-256 hashes of the authorized credentials (No plain-text credentials in the binary)
    private const val AUTH_ID_HASH = "b72e0ffd3ab38a635ec93f9ed11dd293fdd8b0f8b71a8e7eea83aba5e3cd2fa7"
    private const val AUTH_KEY_HASH = "816843b523fc0d3909f0d2be640684e2161c4e8911b5efd2ea885f4c109c78e5"

    /**
     * Compute SHA-256 hash of a string with salt
     */
    fun hashSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val salted = input + SALT
        val hashBytes = digest.digest(salted.toByteArray(StandardCharsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Encrypt text using AES-256 (GCM mode with master key derivation)
     */
    fun encrypt(plainText: String): String {
        return try {
            val keyBytes = MessageDigest.getInstance("SHA-256")
                .digest(MASTER_SECRET.toByteArray(StandardCharsets.UTF_8))
            val keySpec = SecretKeySpec(keyBytes, "AES")

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            // 12-byte fixed IV derived from salt for deterministic decrypt
            val iv = MessageDigest.getInstance("MD5")
                .digest(SALT.toByteArray(StandardCharsets.UTF_8))
                .copyOf(12)
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

            val combined = iv + encryptedBytes
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            // Safe fallback
            Base64.encodeToString(plainText.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
        }
    }

    /**
     * Decrypt AES-256 encrypted text
     */
    fun decrypt(encryptedBase64: String): String {
        return try {
            val data = Base64.decode(encryptedBase64.trim(), Base64.DEFAULT or Base64.NO_WRAP)
            val iv = data.copyOfRange(0, 12)
            val cipherText = data.copyOfRange(12, data.size)

            val keyBytes = MessageDigest.getInstance("SHA-256")
                .digest(MASTER_SECRET.toByteArray(StandardCharsets.UTF_8))
            val keySpec = SecretKeySpec(keyBytes, "AES")

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

            val decryptedBytes = cipher.doFinal(cipherText)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            // Try base64 direct fallback
            try {
                String(Base64.decode(encryptedBase64, Base64.DEFAULT or Base64.NO_WRAP), StandardCharsets.UTF_8)
            } catch (ex: Exception) {
                encryptedBase64
            }
        }
    }

    /**
     * Generate an encrypted iZone config package string
     */
    fun generateEncryptedConfigPackage(jsonPayload: String): String {
        val cipher = encrypt(jsonPayload)
        return "IZONE_ENC://$cipher"
    }

    /**
     * Check if a text is an encrypted config package and decrypt it
     */
    fun isEncryptedConfigPackage(text: String): Boolean {
        return text.trim().startsWith("IZONE_ENC://", ignoreCase = true)
    }

    fun decryptConfigPackage(text: String): String {
        val clean = text.trim().substringAfter("IZONE_ENC://")
        return decrypt(clean)
    }

    /**
     * Constant-time string equality to prevent timing attacks
     */
    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }

    /**
     * Verify credentials against irreversible hashes only.
     * No plain-text username or password exists anywhere in the source or APK.
     */
    fun verifySuperAdmin(inputUser: String, inputPass: String): Boolean {
        val uHash = hashSha256(inputUser.trim())
        val pHash = hashSha256(inputPass)
        return constantTimeEquals(uHash, AUTH_ID_HASH) && constantTimeEquals(pHash, AUTH_KEY_HASH)
    }
}
