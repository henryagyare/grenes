package data.auth

import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId

sealed class AuthState {
    data class Authenticated(val user: User) : AuthState()
    data object NotAuthenticated : AuthState()
    data object Anonymous : AuthState()
}