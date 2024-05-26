package data.users.remote

import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId

interface UserNetworkDataSource {

    suspend fun getUser(uid: UserId): User?


    suspend fun getCurrentUser(): User?

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        token: String,
        uid: String,
        displayName: String?,
        profileAvatar: String?,
    )

    suspend fun updateProfile(
        uid: String,
        email: String,
        photoUrl: String?,
        displayName: String,
        city: String,
        country: String
    )

    suspend fun createProfile(
        uid: String,
        email: String,
        photoUrl: String?,
        displayName: String,
        city: String,
        country: String
    )
}