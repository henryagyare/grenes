package me.ayitinya.grenes.auth

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HashersTest {
    @Test
    fun `test password hashing and verification`() {
        val rawPassword = "password"

        val encodedPassword = Hashers.getHexDigest(rawPassword)
        assertTrue(Hashers.verifyPassword(encodedPassword, rawPassword))
    }
}