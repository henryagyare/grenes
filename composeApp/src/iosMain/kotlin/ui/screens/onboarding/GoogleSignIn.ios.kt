package ui.screens.onboarding

import androidx.compose.runtime.Composable
import me.ayitinya.grenes.data.users.UserId

@Composable
actual fun googleSignIn(): UserId {
    println("Not available on ios")
    return UserId("0")
}