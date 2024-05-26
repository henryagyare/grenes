package data.users.local

import me.ayitinya.grenes.UserTable
import me.ayitinya.grenes.data.users.User

fun UserTable.toUser(): User {
    return User(
        email = this.email,
        city = this.city,
        profileAvatar = this.profileAvatar,
        country = this.country,
        username = this.username,
        createdAt = this.createdAt,
        displayName = this.displayName,
        uid = this.uid
    )
}