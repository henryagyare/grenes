package me.ayitinya.grenes.data.users

import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.badge.BADGES
import me.ayitinya.grenes.data.badge.Badge
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp


internal object UsersTable : Table() {
    val uid = varchar("uid", 64).uniqueIndex()
    val username = varchar("username", length = 64).nullable().uniqueIndex()
    val displayName = varchar("displayName", length = 64).nullable()
    val email = varchar("email", length = 64).uniqueIndex()
    val createdAt = timestamp("createdAt").clientDefault {
        Clock.System.now()
    }
    val password = varchar("password", 256).nullable()
    val country = varchar("country", 256).nullable()
    val city = varchar("city", 256).nullable()
    val profileAvatar = varchar("profileAvatar", 256).nullable()

    val badges = text("badges", eagerLoading = true).default("")

    override val primaryKey = PrimaryKey(uid)
}

fun ResultRow.toUser(): User {
    return User(
        uid = UserId(this[UsersTable.uid]),
        username = this[UsersTable.username],
        displayName = this[UsersTable.displayName],
        email = this[UsersTable.email],
        createdAt = this[UsersTable.createdAt],
        country = this[UsersTable.country],
        city = this[UsersTable.city],
        profileAvatar = this[UsersTable.profileAvatar],
        badges = this[UsersTable.badges].split(",").map { badgeId ->
            BADGES.find { it.uid == badgeId } ?: Badge(
                name = "Unknown",
                description = "Unknown",
                imageUrl = "Unknown",
                uid = "Unknown"
            )
        }
    )
}