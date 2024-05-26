package domain

sealed class AuthenticationState {
    sealed class Onboarding : AuthenticationState() {
        sealed class Email : Onboarding() {
            data object EmailSent : Email()
            data class EmailError(val message: String) : Email()
        }
    }
}