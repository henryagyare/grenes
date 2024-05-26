package me.ayitinya.grenes.routing

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.data.users.UserDao
import me.ayitinya.grenes.di.dbModule
import me.ayitinya.grenes.server.resources.Token
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import kotlin.random.Random
import kotlin.test.assertEquals

class AuthRoutesKtTest : KoinTest {
    private lateinit var db: Db

    @BeforeEach
    fun `setup db`() {
        db = get(named("test")) {
            parametersOf("${Random.nextInt()}")
        }

        TransactionManager.defaultDatabase = db.database

    }

    @Test
    fun `test account creation`() = testApplication {
        init()

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/auth/register") {
            setBody(
                Json.encodeToJsonElement(
                    RegistrationDetails(
                        email = "john.c.breckinridge@altostrat.com",
                        password = "password",
                        displayName = "John C. Breckinridge",
                        fullName = "John Cabell Breckinridge",
                    )
                )
            )

            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test login`() = testApplication {
        init()

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val userDao: UserDao by inject()

        runBlocking {
            userDao.createNewUserWithEmailAndPassword(
                userEmail = "john.c.breckinridge@altostrat.com",
                rawPassword = "password"
            )

            client.post("/auth/login") {
                setBody(
                    Json.encodeToJsonElement(
                        LoginDetails(
                            email = "john.c.breckinridge@altostrat.com", password = "password"
                        )
                    )
                )

                contentType(ContentType.Application.Json)
            }.apply {
                val response: Token = this.body()

                assertEquals(HttpStatusCode.OK, status)
                assert(response.token.isNotEmpty())

            }
        }
    }

    @Test
    fun `login with invalid credentials`() = testApplication {
        init()

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val userDao: UserDao by inject()

        runBlocking {
            userDao.createNewUserWithEmailAndPassword(
                userEmail = "john.c.breckinridge@altostrat.com",
                rawPassword = "password"
            )

            client.post("/auth/login") {
                setBody(
                    Json.encodeToJsonElement(
                        LoginDetails(
                            email = "john.c.breckinridge@altostrat.com", password = "wrong_password"
                        )
                    )
                )

                contentType(ContentType.Application.Json)
            }.apply { assertEquals(HttpStatusCode.Unauthorized, status) }
        }
    }


    companion object {
        @JvmStatic
        @BeforeAll
        fun `start koin`(): Unit {
            GlobalContext.startKoin {
                modules(dbModule)
            }
        }
    }


}