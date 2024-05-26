package data.auth

import dev.gitlive.firebase.auth.*
import kotlinx.coroutines.flow.Flow

class DefaultAuthRepository(private val auth: FirebaseAuth) : AuthRepository {
    override fun getCurrentUser(): Flow<FirebaseUser?> {
        return auth.authStateChanged
    }

    override suspend fun sendSignInLinkToEmail(email: String, onEmailSent: () -> Unit, onError: (String) -> Unit) {
        val actionCodeSettings = ActionCodeSettings(
            url = "https://grenes.page.link/finishSignUp",
            canHandleCodeInApp = true,
            androidPackageName = AndroidPackageName(
                packageName = "me.ayitinya.grenes", installIfNotAvailable = true, minimumVersion = null
            ),
            iOSBundleId = "me.ayitinya.grenes"
        )

        try {
            auth.sendSignInLinkToEmail(email, actionCodeSettings)
            onEmailSent()
        } catch (e: Exception) {
            onError(e.message ?: "Anonymous error")
        }
    }

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun fetchSignInMethodsForEmail(email: String): List<String> {
        return auth.fetchSignInMethodsForEmail(email)
    }

    override suspend fun logout() {
        auth.signOut()
    }
}