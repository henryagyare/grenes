package me.ayitinya.grenes

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.config.mergeWith
import io.ktor.server.testing.testApplication
import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.data.users.UsersTable
import me.ayitinya.grenes.di.dbModule
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.random.Random
import kotlin.test.assertEquals

class ApplicationTest : KoinTest {
    private lateinit var db: Db

    private var currentTest = Random.nextInt()

    @BeforeEach
    fun `setup db`() {
        db = get(named("test")) {
            parametersOf("test${currentTest}")
        }

        TransactionManager.defaultDatabase = db.database
    }

    @Test
    fun testRoot() = testApplication {

        application {
            main(testing = true)
        }

        environment {
            config =
                ApplicationConfig("test_application.yaml").mergeWith(MapApplicationConfig("ktor.environment" to "test"))
        }


        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }



    companion object {
        @JvmStatic
        @BeforeAll
        fun `start koin`(): Unit {
            startKoin {
                modules(dbModule)
            }
        }
    }
}