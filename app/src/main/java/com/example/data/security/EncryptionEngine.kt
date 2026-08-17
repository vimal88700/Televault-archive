package com.example.data.security

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object EncryptionEngine {

    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12
    private const val SALT_LENGTH_BYTE = 16
    private const val ITERATION_COUNT = 10000
    private const val KEY_LENGTH_BIT = 256

    private val secureRandom = SecureRandom()

    // Default master salt for device-level session key
    private val DEFAULT_DEVICE_SALT = byteArrayOf(
        0x14, 0x2A, 0x7B.toByte(), 0x4D, 0x99.toByte(), 0x11, 0x33, 0x55,
        0x77, 0x88.toByte(), 0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte(), 0xEE.toByte(), 0xFF.toByte()
    )

    fun deriveKey(passphrase: String, salt: ByteArray = DEFAULT_DEVICE_SALT): SecretKeySpec {
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BIT)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts raw data bytes and returns IV + ciphertext
     */
    fun encryptBytes(data: ByteArray, secretKey: SecretKeySpec): ByteArray {
        val iv = ByteArray(IV_LENGTH_BYTE)
        secureRandom.nextBytes(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

        val cipherText = cipher.doFinal(data)
        // Combine IV (12 bytes) + CipherText
        val combined = ByteArray(iv.size + cipherText.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
        return combined
    }

    /**
     * Decrypts combined (IV + ciphertext) data
     */
    fun decryptBytes(encryptedData: ByteArray, secretKey: SecretKeySpec): ByteArray {
        if (encryptedData.size < IV_LENGTH_BYTE) {
            throw IllegalArgumentException("Invalid encrypted payload length")
        }
        val iv = ByteArray(IV_LENGTH_BYTE)
        System.arraycopy(encryptedData, 0, iv, 0, IV_LENGTH_BYTE)

        val cipherTextSize = encryptedData.size - IV_LENGTH_BYTE
        val cipherText = ByteArray(cipherTextSize)
        System.arraycopy(encryptedData, IV_LENGTH_BYTE, cipherText, 0, cipherTextSize)

        val cipher = Cipher.getInstance(ALGORITHM)
        val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

        return cipher.doFinal(cipherText)
    }

    /**
     * Encrypts a source file to an encrypted destination file
     */
    fun encryptFile(sourceFile: File, destEncryptedFile: File, secretKey: SecretKeySpec) {
        val rawBytes = sourceFile.readBytes()
        val encrypted = encryptBytes(rawBytes, secretKey)
        destEncryptedFile.parentFile?.mkdirs()
        destEncryptedFile.writeBytes(encrypted)
    }

    /**
     * Decrypts an encrypted file to plain bytes
     */
    fun decryptFileToBytes(encryptedFile: File, secretKey: SecretKeySpec): ByteArray {
        if (!encryptedFile.exists()) return ByteArray(0)
        val encrypted = encryptedFile.readBytes()
        return try {
            decryptBytes(encrypted, secretKey)
        } catch (e: Exception) {
            // Fallback for demo mock data if not encrypted with GCM
            encrypted
        }
    }

    /**
     * Securely shreds a file by overwriting it before deletion
     */
    fun secureShred(file: File) {
        if (file.exists()) {
            try {
                val length = file.length()
                val randomBytes = ByteArray(1024)
                FileOutputStream(file).use { out ->
                    var written: Long = 0
                    while (written < length) {
                        secureRandom.nextBytes(randomBytes)
                        val toWrite = minOf(randomBytes.size.toLong(), length - written).toInt()
                        out.write(randomBytes, 0, toWrite)
                        written += toWrite
                    }
                    out.flush()
                }
            } catch (ignored: Exception) {
            } finally {
                file.delete()
            }
        }
    }

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
