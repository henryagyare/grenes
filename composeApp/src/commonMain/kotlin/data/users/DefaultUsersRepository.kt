package data.users

import data.users.local.DefaultUsersLocalDataSource
import data.users.local.UsersLocalDataSource
import data.users.remote.UserNetworkDataSource
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId

class DefaultUsersRepository(
    private val userNetworkDataSource: UserNetworkDataSource,
    private val usersLocalDataSource: UsersLocalDataSource,
) : UsersRepository {
    override suspend fun getUser(uid: UserId): User? {
        return userNetworkDataSource.getUser(uid)
    }

    override suspend fun getUser(): User? {
        try {
            return userNetworkDataSource.getCurrentUser()
        } catch (e: Exception) {
            println("Error: ${e.message} - ${e.cause}")
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        token: String,
        uid: String,
        displayName: String?,
        profileAvatar: String?,
    ) {
        userNetworkDataSource.createUserWithEmailAndPassword(
            email = email,
            password = password,
            token = token,
            uid = uid,
            displayName = displayName,
            profileAvatar = profileAvatar
        )
    }

    override suspend fun updateProfile(
        uid: String,
        email: String,
        photoUrl: String?,
        displayName: String,
        city: String,
        country: String,
    ) {
        userNetworkDataSource.updateProfile(
            uid = uid,
            email = email,
            photoUrl = photoUrl,
            displayName = displayName,
            city = city,
            country = country
        )
    }

    override suspend fun createProfile(
        uid: String,
        email: String,
        photoUrl: String?,
        displayName: String,
        city: String,
        country: String,
    ) {
        userNetworkDataSource.createProfile(
            uid = uid,
            email = email,
            photoUrl = photoUrl,
            displayName = displayName,
            city = city,
            country = country
        )
    }
}