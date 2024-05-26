package domain

import data.app.AppPreferences
import data.auth.AuthRepository
import data.auth.AuthState
import data.users.UsersRepository
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId


/**
 * Default implementation of the AuthenticationUseCase interface.
 *
 * @property appPreferences Instance of AppPreferences for managing application preferences.
 * @property usersRepository Instance of UsersRepository for managing user data.
 * @property authRepository Instance of AuthRepository for managing authentication.
 */
class DefaultAuthenticationUseCase(
    private val appPreferences: AppPreferences,
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository,
    private val httpClient: HttpClient,
) : AuthenticationUseCase {
    override fun getAuthState(): Flow<AuthState> {
        return authRepository.getCurrentUser().transform { firebaseUser ->
            if (firebaseUser == null) {
                emit(AuthState.NotAuthenticated)
            } else {
                if (firebaseUser.isAnonymous) {
                    emit(AuthState.Anonymous)
                } else {
                    val user = try {
                        usersRepository.getUser()
                    } catch (e: Exception) {
                        null
                    }

                    if (user != null) {
                        emit(AuthState.Authenticated(user))

                    } else {
                        emit(
                            AuthState.Authenticated(
                                User(
                                    uid = UserId(firebaseUser.uid),
                                    createdAt = Clock.System.now(),
                                    email = firebaseUser.email ?: ""
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = authRepository.getCurrentUser().take(1).firstOrNull()

        return if (firebaseUser != null) {
            val user = usersRepository.getUser(UserId(firebaseUser.uid))
            if (user != null) {
                return user
            } else null
        } else null
    }

    override suspend fun sendSignInLinkToEmail(
        email: String,
        onEmailSent: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            authRepository.sendSignInLinkToEmail(email, onEmailSent, onError)
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun createUserWithEmailAndPassword(email: String, password: String) {
        try {
            val authResult = authRepository.createUserWithEmailAndPassword(email, password)

            if (authResult.user == null) {
                throw Exception("User is null")
            }

            appPreferences.setIsOnBoardingComplete(false)

            authResult.user!!.getIdToken(false).let { token ->
                if (token != null) {
                    setAuthToken(token)
                }
            }

        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        try {
            val authResult = authRepository.signInWithEmailAndPassword(email, password)

            if (authResult.user == null) {
                throw Exception("User is null")
            }


            authResult.user!!.getIdToken(false).let { token ->
                if (token != null) {
                    setAuthToken(token)
                }

                usersRepository.getUser().let { user ->
                    if (user != null) {
                        appPreferences.setIsOnBoardingComplete(true)
                    } else {
                        appPreferences.setIsOnBoardingComplete(false)
                    }
                }
            }

        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun fetchSignInMethodsForEmail(email: String): List<String> =
        authRepository.fetchSignInMethodsForEmail(email)

    override suspend fun logout() {
        try {
            authRepository.logout()

            setAuthToken("")

        } catch (e: Exception) {
            throw e
        }
    }

    private fun setAuthToken(token: String) {
        httpClient.config {
            defaultRequest {
                bearerAuth(token)
            }
        }
    }
}