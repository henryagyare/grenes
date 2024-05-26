package navigation

import data.auth.AuthState

data class SharedState(
    val setupComplete: Boolean = false,
    val authState: AuthState = AuthState.NotAuthenticated,
    val initializing: Boolean = true
)