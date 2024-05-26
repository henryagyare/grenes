package me.ayitinya.grenes.routing

import io.ktor.client.request.*
import io.ktor.server.testing.*
import me.ayitinya.grenes.main
import kotlin.test.Test

class UserRoutesKtTest {

    @Test
    fun testGetEmptyRoute() = testApplication {
        application {
            main()
        }
        client.get("").apply {
            TODO("Please write your test here")
        }
    }
}