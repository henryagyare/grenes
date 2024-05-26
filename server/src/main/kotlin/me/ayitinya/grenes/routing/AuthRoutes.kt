package me.ayitinya.grenes.routing

import com.auth0.jwk.JwkProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import me.ayitinya.grenes.auth.AuthStates
import me.ayitinya.grenes.auth.authenticateUser
import me.ayitinya.grenes.auth.jwtToken
import me.ayitinya.grenes.data.users.UserDao
import org.koin.ktor.ext.inject
import java.util.*

@Serializable
data class LoginDetails(val email: String, val password: String)

@Serializable
data class RegistrationDetails(
    val email: String,
    val password: String,
    val displayName: String,
    val fullName: String,
    @Contextual val locationId: UUID? = null
)

internal fun Route.authRoutes(
    privateKeyString: String, issuer: String, audience: String, jwkProvider: JwkProvider
) {

    val userDao: UserDao by inject()

    post<AuthResource.Login> {

        val loginDetails = call.receive<LoginDetails>()

        val user = authenticateUser(
            email = loginDetails.email,
            rawPassword = loginDetails.password
        )

        when (user) {
            is AuthStates.Authenticated -> {
                val jwtToken = jwtToken(
                    email = loginDetails.email,
                    privateKeyString = privateKeyString,
                    issuer = issuer,
                    audience = audience,
                    jwkProvider = jwkProvider
                )
                call.respond(hashMapOf("token" to jwtToken))
            }

            AuthStates.Error -> call.respond(HttpStatusCode.InternalServerError)
            AuthStates.InvalidCredentials -> call.respond(
                HttpStatusCode.Unauthorized, "Invalid credentials"
            )

            AuthStates.UserNotFound -> call.respond(HttpStatusCode.NotFound, "User not found")
        }

    }

    post<AuthResource.Logout> {
        call.respond(HttpStatusCode.OK)
    }

    post<AuthResource.Register> {
        try {
            val userDetails = call.receive<RegistrationDetails>()

            userDao.createNewUserWithEmailAndPassword(
                userEmail = userDetails.email,
                rawPassword = userDetails.password
            )

            val jwtToken = jwtToken(
                email = userDetails.email,
                privateKeyString = privateKeyString,
                issuer = issuer,
                audience = audience,
                jwkProvider = jwkProvider
            )
            call.respond(hashMapOf("token" to jwtToken))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Error")
        }
    }

}