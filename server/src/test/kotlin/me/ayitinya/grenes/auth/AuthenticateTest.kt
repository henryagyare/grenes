package me.ayitinya.grenes.auth

import com.auth0.jwk.JwkProviderBuilder
import kotlinx.coroutines.runBlocking
import me.ayitinya.grenes.data.Db
//import me.ayitinya.grenes.data.location.LocationDao
import me.ayitinya.grenes.data.users.UserDao
import me.ayitinya.grenes.di.dbModule
import me.ayitinya.grenes.usersList
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class AuthenticateTest : KoinTest {

    private val privateKeyString =
        "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAtfJaLrzXILUg1U3N1KV8yJr92GHn5OtYZR7qWk1Mc4cy4JGjklYup7weMjBD9f3bBVoIsiUVX6xNcYIr0Ie0AQIDAQABAkEAg+FBquToDeYcAWBe1EaLVyC45HG60zwfG1S4S3IB+y4INz1FHuZppDjBh09jptQNd+kSMlG1LkAc/3znKTPJ7QIhANpyB0OfTK44lpH4ScJmCxjZV52mIrQcmnS3QzkxWQCDAiEA1Tn7qyoh+0rOO/9vJHP8U/beo51SiQMw0880a1UaiisCIQDNwY46EbhGeiLJR1cidr+JHl86rRwPDsolmeEF5AdzRQIgK3KXL3d0WSoS//K6iOkBX3KMRzaFXNnDl0U/XyeGMuUCIHaXv+n+Brz5BDnRbWS+2vkgIe9bUNlkiArpjWvX+2we"
    private val issuer = "http://0.0.0.0:8080/"
    private val audience = "http://0.0.0.0:8080/hello"

    private val jwkProvider = JwkProviderBuilder(issuer).cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES).build()


    private lateinit var database: Db
    private val userDao: UserDao by inject()
//    private val locationDao: LocationDao by inject()

    private var currentTest = Random.nextInt()

    private val user = usersList.first()


    @BeforeEach
    fun setUp() {
        database = get<Db>(named("test")) {
            parametersOf("test${currentTest}")
        }

        runBlocking {

                userDao.createNewUserWithEmailAndPassword(
                    userEmail = user.email,
                    rawPassword = "password",
                )
            }

    }

    @Test
    fun `authenticate User with right data`() {
        runBlocking {
            val authenticate = authenticateUser(
                email = user.email!!,
                rawPassword = "password"
            )
            assert(authenticate is AuthStates.Authenticated)
        }
    }

    @Test
    fun `authenticate User with wrong password`() {
        runBlocking {
            val authenticate = authenticateUser(
                email = user.email!!,
                rawPassword = "wrong password"
            )
            assert(authenticate is AuthStates.InvalidCredentials)
        }
    }

    @Test
    fun `authenticate with invalid credentials`() {
        runBlocking {
            val authenticate = authenticateUser(
                email = "sulleyrudy@fad.com",
                rawPassword = "fadf"
            )
            assert(authenticate is AuthStates.UserNotFound)
        }
    }

    @Test
    fun `get Jwt Token`() {
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