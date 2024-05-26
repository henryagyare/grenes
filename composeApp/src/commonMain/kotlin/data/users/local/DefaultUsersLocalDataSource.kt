package data.users.local

import me.ayitinya.grenes.Database
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId

class DefaultUsersLocalDataSource(database: Database) : UsersLocalDataSource {
    private val userTableQueries = database.userQueries

    override suspend fun saveToDb(user: User) {
        userTableQueries.insert(
            uid = user.uid,
            city = user.city,
            createdAt = user.createdAt,
            country = user.country,
            displayName = user.displayName,
            email = user.email,
            username = user.username,
            profileAvatar = user.profileAvatar
        )
    }

    override suspend fun read(userId: UserId): User {
        return userTableQueries.selectByUid(userId).executeAsOne().toUser()
    }
}