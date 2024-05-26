package ui.screens.onboarding.authentication

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class AuthFlowState {
    data object Loading : AuthFlowState()
    data object Idle : AuthFlowState()
    data object EmailSent : AuthFlowState()
    data class Error(val message: String) : AuthFlowState()

    data class UserExist(val signInMethods: List<String>) : AuthFlowState()

    data object UserDoesNotExist : AuthFlowState()

    data object UserCreated : AuthFlowState()

    data class UserLoggedIn(val isSetupComplete: Boolean) : AuthFlowState()
}

@Stable
interface UiState {
    val email: String
    val password: String
    val authFlowState: AuthFlowState
}

internal class MutableUiState : UiState {
    override var email by mutableStateOf("")
    override var password by mutableStateOf("")
    override var authFlowState by mutableStateOf<AuthFlowState>(AuthFlowState.Idle)
}