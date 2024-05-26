package domain

import data.auth.AuthState
import kotlinx.coroutines.flow.Flow
import me.ayitinya.grenes.data.users.User


interface AuthenticationUseCase {

    /**
     * Get the current authentication state.
     *
     * @return AuthState representing the current authentication state.
     */
    fun getAuthState(): Flow<AuthState>

    /**
     * Get the current authentication state.
     *
     * @return AuthState representing the current authentication state.
     */
    suspend fun getCurrentUser(): User?

    /**
     * Send a sign-in link to the provided email address.
     *
     * @param email The email address to send the sign-in link to.
     * @param onEmailSent Callback function to be called when the email has been sent.
     * @param onError Callback function to be called when an error occurs.
     */
    suspend fun sendSignInLinkToEmail(
        email: String,
        onEmailSent: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Create a new user with the provided email and password.
     *
     * @param email The email address of the new user.
     * @param password The password of the new user.
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String)

    /**
     * Sign in a user with the provided email and password.
     *
     * @param email The email address of the user.
     * @param password The password of the user.
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String)

    /**
     * Fetch the sign-in methods for the provided email address.
     *
     * @param email The email address to fetch the sign-in methods for.
     * @return List<String> representing the sign-in methods for the provided email address.
     */
    suspend fun fetchSignInMethodsForEmail(email: String): List<String>

    /**
     * Log out the current user.
     */
    suspend fun logout()
}