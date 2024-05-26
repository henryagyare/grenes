package me.ayitinya.grenes.auth.firebase

import com.google.firebase.auth.FirebaseToken
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*


class FirebaseConfig(name: String?) : AuthenticationProvider.Config(name) {
    internal var authHeader: (ApplicationCall) -> HttpAuthHeader? = { call ->
        call.request.parseAuthorizationHeaderOrNull()
    }

    var firebaseAuthenticationFunction: AuthenticationFunction<FirebaseToken> = {
        throw NotImplementedError(FirebaseImplementationError)
    }

    fun validate(validate: suspend ApplicationCall.(FirebaseToken) -> FirebaseUserPrincipal?) {
        firebaseAuthenticationFunction = validate
    }
}

data class FirebaseUserPrincipal(val uid: String, val displayName: String, val roles: List<String> = emptyList()) :
    Principal

fun ApplicationRequest.parseAuthorizationHeaderOrNull(): HttpAuthHeader? = try {
    parseAuthorizationHeader()
} catch (exception: IllegalArgumentException) {
    println("failed to parse token")
    null
}

private const val FirebaseImplementationError =
    "Firebase auth validate function is not specified, use firebase {valide {...}} to fix this"