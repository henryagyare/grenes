package me.ayitinya.grenes.routing

import io.ktor.client.request.*
import io.ktor.server.testing.*
import me.ayitinya.grenes.main
import kotlin.test.Test


//import io.ktor.client.call.*
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.http.ContentType
//import io.ktor.http.HttpStatusCode
//import io.ktor.http.contentType
//import io.ktor.serialization.kotlinx.json.json
//import io.ktor.server.config.ApplicationConfig
//import io.ktor.server.config.MapApplicationConfig
//import io.ktor.server.config.mergeWith
//import io.ktor.server.testing.ApplicationTestBuilder
//import io.ktor.server.testing.testApplication
//import kotlinx.coroutines.runBlocking
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.encodeToJsonElement
//import me.ayitinya.grenes.data.Db
//import me.ayitinya.grenes.data.users.UserDao
//import me.ayitinya.grenes.data.users.UsersTable
//import me.ayitinya.grenes.di.dbModule
//import me.ayitinya.grenes.main
//import me.ayitinya.grenes.server.resources.Token
//import org.jetbrains.exposed.sql.transactions.TransactionManager
//import org.jetbrains.exposed.sql.transactions.transaction
//import org.junit.jupiter.api.BeforeAll
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.koin.core.context.GlobalContext
//import org.koin.core.parameter.parametersOf
//import org.koin.core.qualifier.named
//import org.koin.ktor.plugin.Koin
//import org.koin.test.KoinTest
//import org.koin.test.get
//import org.koin.test.inject
//import kotlin.random.Random
//import kotlin.test.assertEquals

//class UserRoutesTest {
//    private lateinit var db: Db
//
//    @BeforeEach
//    fun `setup db`() {
//        db = get(named("test")) {
//            parametersOf("${Random.nextInt()}")
//        }
//
//        TransactionManager.defaultDatabase = db.database
//
//        transaction {
//            val res = UserEntity.find { UsersTable.email eq "" }.toList()
//            println("res = $res")
//        }
//    }
//
//
//    @Test
//    fun testGetUsers() = testApplication {
//        application {
//            main()
//        }
//        client.get("/users").apply {
//            TODO("Please write your test here")
//        }
//    }
//}
class UserRoutesTest {

    @Test
    fun testGet() = testApplication {
        application {
            main()
        }
        client.get("/").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPost() = testApplication {
        application {
            main()
        }
        client.post("/").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPut() = testApplication {
        application {
            main()
        }
        client.put("/").apply {
            TODO("Please write your test here")
        }
    }
}