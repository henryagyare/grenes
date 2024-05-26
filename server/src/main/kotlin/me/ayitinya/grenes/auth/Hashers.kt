package me.ayitinya.grenes.auth

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@OptIn(ExperimentalStdlibApi::class)
object Hashers {
    private const val ALGORITHM = "PBKDF2WithHmacSHA512"
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SECRET = "SomeRandomSecret"

    private fun generateRandomSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    private fun generateHash(password: String, salt: String): String {
        val combinedSalt = "$salt$SECRET".toByteArray()
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
        val key: SecretKey = factory.generateSecret(spec)
        val hash: ByteArray = key.encoded
        return hash.toHexString()
    }

    fun getHexDigest(password: String): String {
        val salt = generateRandomSalt().toHexString()
        val hash = generateHash(password, salt)

        return "$salt\$$hash"
    }

    fun verifyPassword(encodedPassword: String, rawPassword: String): Boolean {
        val salt = encodedPassword.substringBefore("$")
        val hash = generateHash(rawPassword, salt)
        val encodedHash = "$salt\$$hash"

        return encodedHash == encodedPassword
    }
}