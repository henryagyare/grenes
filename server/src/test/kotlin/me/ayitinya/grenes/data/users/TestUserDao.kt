package me.ayitinya.grenes.data.users

import kotlinx.coroutines.runBlocking
import me.ayitinya.grenes.auth.Hashers
import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.di.dbModule
import me.ayitinya.grenes.usersList
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import kotlin.random.Random

class TestUserDao : KoinTest {

    private lateinit var database: Db
    private val sut: UserDao by inject()

    private var currentTest = Random.nextInt()

    @BeforeEach
    fun setUp() {
        database = get<Db>(named("test")) {
            parametersOf("test${currentTest}")
        }
    }

    @Test
    fun allUsers() {
        runBlocking {
            usersList.forEach { user ->
                transaction {
                    UsersTable.insert {
                        it[UsersTable.email] = user.email
                        it[UsersTable.password] = Hashers.getHexDigest("password")
                        it[UsersTable.displayName] = user.displayName
                        it[UsersTable.createdAt] = user.createdAt
                    }
                }

            }
            val users = sut.allUsers()
            assertEquals(2, users.size)
        }
    }

    @Test
    fun addNewUser() {
        val fullName = "Test User"
        val email = "william.strong@my-own-personal-domain.com"
        val password = "password"
        val displayName = "Test User"


        runBlocking {
            sut.createNewUserWithEmailAndPassword(
                userEmail = email,
                rawPassword = password
            )
            val user = UsersTable.select { UsersTable.email eq email }.map {
                User(
                    uid = UserId(it[UsersTable.uid]),
                    displayName = it[UsersTable.displayName],
                    email = it[UsersTable.email],
                    createdAt = it[UsersTable.createdAt],
                )
            }.first()
            assertEquals(email, user.email)
            assertEquals(displayName, user.displayName)
        }
    }

    @Test
    fun editUser() {
    }

    @Test
    fun deleteUser() {
    }

    @Test
    fun getUser() {
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