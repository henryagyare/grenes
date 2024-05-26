package me.ayitinya.grenes.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.ayitinya.grenes.auth.firebase.FirebaseUserPrincipal
import me.ayitinya.grenes.auth.firebase.firebase
import me.ayitinya.grenes.routing.authRoutes
import java.util.concurrent.TimeUnit

fun Application.configureAuthentication() {
    val privateKeyString = environment.config.property("jwt.privateKey").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    val jwkProvider = JwkProviderBuilder(issuer).cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES).build()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }

        firebase {
            validate {
                FirebaseUserPrincipal(it.uid, it.name.orEmpty())
            }
        }
    }


    routing {
        authRoutes(privateKeyString, issuer, audience, jwkProvider)
    }

}