package data.auth

import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<FirebaseUser?>

    suspend fun sendSignInLinkToEmail(
        email: String,
        onEmailSent: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult

    suspend fun fetchSignInMethodsForEmail(email: String): List<String>

    suspend fun logout()
}