package me.ayitinya.grenes.auth

sealed class AuthStates {
    data object Authenticated : AuthStates()
    data object InvalidCredentials : AuthStates()
    data object UserNotFound : AuthStates()
    data object Error : AuthStates()
}