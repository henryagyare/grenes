package me.ayitinya.grenes.routing

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord.UpdateRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.util.logging.*
import me.ayitinya.grenes.auth.firebase.FIREBASE_AUTH
import me.ayitinya.grenes.auth.firebase.FirebaseUserPrincipal
import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserDao
import org.koin.ktor.ext.inject

//internal val LOGGER = KtorSimpleLogger("UserRoutes")

fun Route.userRoutes() {
    val userDao by inject<UserDao>()

    get<UsersResource> {
        try {
            val users = userDao.allUsers()
            call.respond(mapOf("users" to users))

        } catch (exception: Exception) {
            println(exception)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    authenticate(FIREBASE_AUTH) {
        get<UsersResource.SessionUserDetails> {
            this.context.authentication.principal<FirebaseUserPrincipal>()?.let { principal ->

                val user = userDao.getUserById(uid = principal.uid)

                if (user == null) call.respond(HttpStatusCode.NotFound)
                else call.respond(user)
            }
        }
    }

    authenticate(FIREBASE_AUTH) {
        post<UsersResource.Register> {
            this.context.authentication.principal<FirebaseUserPrincipal>()?.let { principal ->
                println("User: $principal")
            }
        }
    }

    authenticate(FIREBASE_AUTH) {
        post<UsersResource.CreateUserWithUid> {
            this.context.authentication.principal<FirebaseUserPrincipal>()?.let { principal ->
                val user = FirebaseAuth.getInstance().getUser(principal.uid)

                user.email?.let { email ->
                    userDao.createNewUserWithUidAndEmail(principal.uid, email)
                }
            }
        }
    }

    authenticate(FIREBASE_AUTH) {
        post<UsersResource.SessionUserDetails> {
            this.context.authentication.principal<FirebaseUserPrincipal>()?.let { principal ->
                try {
                    val user = call.receive<User>()
                    Db.query {
                        userDao.createNewUserWithUidAndEmail(principal.uid, user.email)
                        userDao.updateUser(principal.uid, user)
                    }

                    val updateRequest =
                        UpdateRequest(principal.uid).setDisplayName(user.displayName).setPhotoUrl(user.profileAvatar)
                    FirebaseAuth.getInstance().updateUser(updateRequest)

                    call.respond(HttpStatusCode.OK)

                } catch (exception: Exception) {
                    println(exception)
                    exception.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }

    authenticate(FIREBASE_AUTH) {
        put<UsersResource.SessionUserDetails> {
            this.context.authentication.principal<FirebaseUserPrincipal>()?.let { principal ->
                try {
                    val user = call.receive<User>()
                    userDao.updateUser(principal.uid, user)

                    val updateRequest =
                        UpdateRequest(principal.uid).setDisplayName(user.displayName).setPhotoUrl(user.profileAvatar)
                    FirebaseAuth.getInstance().updateUser(updateRequest)

                    call.respond(HttpStatusCode.OK)

                } catch (exception: Exception) {
                    println(exception)
                    exception.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}