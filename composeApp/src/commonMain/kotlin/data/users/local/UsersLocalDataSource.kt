package data.users.local

import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId

interface UsersLocalDataSource {
    suspend fun saveToDb(user: User)

    suspend fun read(userId: UserId): User?
}