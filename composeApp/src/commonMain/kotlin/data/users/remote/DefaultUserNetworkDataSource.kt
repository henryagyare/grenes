package data.users.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId


class DefaultUserNetworkDataSource(private val httpClient: HttpClient) : UserNetworkDataSource {

    override suspend fun getUser(uid: UserId): User? {
        return try {
            withContext(Dispatchers.IO) {
                httpClient.get("/users/me").body<User>()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    override suspend fun getCurrentUser(): User {
        return try {
            withContext(Dispatchers.IO) {
                val res = httpClient.get("/users/me")

                if (res.status == HttpStatusCode.NotFound) {
                    throw Exception("User not found")
                }

                res.body<User>()
            }
        } catch (cause: Throwable) {
            cause.printStackTrace()
            throw cause
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
        try {
            httpClient.post("/users/create-user-with-uid") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                setBody(
                    User(
                        uid = UserId(uid),
                        displayName = displayName,
                        email = email,
                        createdAt = Clock.System.now(),
                        profileAvatar = profileAvatar,
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }


    override suspend fun updateProfile(
        uid: String,
        email: String,
        photoUrl: String?,
        displayName: String,
        city: String,
        country: String,
    ) {
        try {
            httpClient.put("/users/me") {
                contentType(ContentType.Application.Json)
                setBody(
                    User(
                        uid = UserId(uid),
                        displayName = displayName,
                        email = email,
                        createdAt = Clock.System.now(),
                        profileAvatar = photoUrl,
                    )
                )
            }
        } catch (exception: Throwable) {
            println("Error: ${exception.message} - ${exception.cause}")
            exception.printStackTrace()
            throw exception
        }
    }

    override suspend fun createProfile(
        uid: String,
        email: String,
        photoUrl: String?,
        displayName: String,
        city: String,
        country: String,
    ) {
        try {
            httpClient.post("/users/me") {
                contentType(ContentType.Application.Json)
                setBody(
                    User(
                        uid = UserId(uid),
                        displayName = displayName,
                        email = email,
                        createdAt = Clock.System.now(),
                        profileAvatar = photoUrl,
                    )
                )
            }
        } catch (exception: Throwable) {
            println("Error: ${exception.message} - ${exception.cause}")
            exception.printStackTrace()
            throw exception
        }
    }
}

// you can also use firebase cloud functions to notify the backend of the created user
// or send the information to the backend server to create the firebase user
// then login with the uid and password on the frontend