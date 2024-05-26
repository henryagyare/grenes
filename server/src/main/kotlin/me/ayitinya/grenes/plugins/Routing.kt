package me.ayitinya.grenes.plugins

import Greeting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.ayitinya.grenes.auth.RoleBasedAuthorizationPlugin
import me.ayitinya.grenes.auth.Roles
import me.ayitinya.grenes.routing.challengeRoutes
import me.ayitinya.grenes.routing.feedRoutes
import me.ayitinya.grenes.routing.userRoutes

fun Route.rbac(
    vararg hasAnyRole: Roles,
    build: Route.() -> Unit
) {
    install(RoleBasedAuthorizationPlugin) { roles = hasAnyRole.toSet() }
    build()
}

fun Application.configureRouting() {
    routing {

        staticResources("/.well-known", "assets")

        userRoutes()
        challengeRoutes()
        feedRoutes()

        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
    }

    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("Baggage")
        allowHeader("Sentry-Trace")
        anyHost()
    }
}