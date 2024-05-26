package me.ayitinya.grenes.config.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import me.ayitinya.grenes.auth.Roles

class FirebaseAdmin {
    private val serviceAccount =
        this::class.java.classLoader?.getResourceAsStream("service-account.json")
    private val options = FirebaseOptions.builder().setCredentials(
        GoogleCredentials.fromStream(serviceAccount)
    ).setStorageBucket("grenes-1759f.appspot.com").build()

    init {
        FirebaseApp.initializeApp(options)

        try {
            FirebaseAuth.getInstance().getUserByEmail(System.getenv("DEFAULT_USER_EMAIL"))
        } catch (e: FirebaseAuthException) {
            val createUserRequest =
                UserRecord.CreateRequest()
                    .setEmail(System.getenv("DEFAULT_USER_EMAIL"))
                    .setPassword(System.getenv("DEFAULT_USER_PASSWORD"))
                    .setDisplayName("Admin")
                    .setDisabled(false)
                    .setEmailVerified(true)

            val createUser = FirebaseAuth.getInstance().createUser(createUserRequest)

            val claims: MutableMap<String, Any> = HashMap()
            claims["roles"] = listOf(Roles.SUPER_ADMIN.role)

            FirebaseAuth.getInstance().setCustomUserClaims(createUser.uid, claims)
        }
    }
}