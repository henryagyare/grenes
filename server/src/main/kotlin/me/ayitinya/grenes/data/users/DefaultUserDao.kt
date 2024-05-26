package me.ayitinya.grenes.data.users

import io.ktor.util.logging.*
import me.ayitinya.grenes.auth.Hashers.getHexDigest
import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.data.badge.Badge
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*


internal val LOGGER = KtorSimpleLogger("com.example.RequestTracePlugin")

class DefaultUserDao : UserDao {

    override suspend fun allUsers(): List<User> = Db.query {
        UsersTable.selectAll().map {
            it.toUser()
//            User(
//                uid = it[UsersTable.uid],
//                username = it[UsersTable.username],
//                displayName = it[UsersTable.displayName],
//                email = it[UsersTable.email],
//                createdAt = it[UsersTable.createdAt],
//                city = it[UsersTable.city],
//                country = it[UsersTable.country]
//            )
        }
    }

    override suspend fun updateUser(uid: String, user: User) {
        Db.query {
            UsersTable.select { UsersTable.uid eq uid }.distinct().firstOrNull().let {
                if (it != null) {
                    UsersTable.update({ UsersTable.uid eq uid }) { updateStatement ->
                        updateStatement[displayName] = user.displayName
                        updateStatement[username] = user.username
                        updateStatement[city] = user.city
                        updateStatement[country] = user.country

                        user.badges.fold("") { acc, badge ->
                            acc + badge.uid + ","
                        }.let { badges ->
                            updateStatement[UsersTable.badges] = badges
                        }
                    }
                } else {
                    createNewUserWithUidAndEmail(uid = uid, email = user.email)
                    updateUser(uid = uid, user = user) // risk of recursion
                }
            }
        }
    }

    override suspend fun createNewUserWithUidAndEmail(uid: String, email: String): Int {
        return Db.query {
            val insertStatement = UsersTable.insert {
                it[UsersTable.email] = email
                it[UsersTable.uid] = uid
            }

            insertStatement.insertedCount
        }
    }


    override suspend fun createNewUserWithEmailAndPassword(
        userEmail: String, rawPassword: String,
    ) {
        Db.query {
            UsersTable.insert {
                it[uid] = UUID.randomUUID().toString()
                it[email] = userEmail
                it[password] = getHexDigest(rawPassword)
            }
        }
    }

    override suspend fun deleteUser(uid: String) {
        Db.query {
            UsersTable.deleteWhere { UsersTable.uid eq uid }
        }
    }

    override suspend fun getUserByEmail(email: String): User? = Db.query {
        return@query UsersTable.select { UsersTable.email eq email }.firstOrNull()?.let {
            User(
                uid = UserId(it[UsersTable.uid]),
                displayName = it[UsersTable.displayName],
                email = it[UsersTable.email],
                createdAt = it[UsersTable.createdAt],
                city = it[UsersTable.city],
                country = it[UsersTable.country],
                profileAvatar = it[UsersTable.profileAvatar]
            )
        }
    }

    override suspend fun getUserById(uid: String): User? = Db.query {
        UsersTable.select { UsersTable.uid eq uid }.firstOrNull()?.toUser()
    }
}