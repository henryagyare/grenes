package me.ayitinya.grenes.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import me.ayitinya.grenes.auth.firebase.FirebaseUserPrincipal

enum class Roles(val role: String) {
    SUPER_ADMIN("superAdmin")
}

class PluginConfiguration {
    var roles: Set<Roles> = emptySet()
}

val RoleBasedAuthorizationPlugin = createRouteScopedPlugin(
    name = "RbacPlugin",
    createConfiguration = ::PluginConfiguration
) {
    val roles = pluginConfig.roles

    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val tokenRoles = getRolesFromFirebaseClaims(call)
            val authorized = roles.any { it.role in tokenRoles }
            if (!authorized) {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

private fun getRolesFromFirebaseClaims(call: ApplicationCall): List<String> {
    return call.principal<FirebaseUserPrincipal>()!!.roles
}