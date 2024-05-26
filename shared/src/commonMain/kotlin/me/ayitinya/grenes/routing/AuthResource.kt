package me.ayitinya.grenes.routing

import io.ktor.resources.*

@Resource("/auth")
class AuthResource() {
    @Resource("login")
    data class Login(val parent: AuthResource = AuthResource())

    @Resource("/logout")
    data class Logout(val parent: AuthResource = AuthResource())

    @Resource("/register")
    data class Register(val parent: AuthResource = AuthResource())
}
