package di

import dev.gitlive.firebase.auth.FirebaseAuth
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun httpClientModule(url: URLBuilder) = module {
    single(createdAtStart = true) {
        HttpClient {

            val firebase: FirebaseAuth = get()

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            install(HttpCache)

            install(DefaultRequest) {
                header("Content-Type", "application/json")

                url {
                    takeFrom(url)
                }

                if (firebase.currentUser != null) {
                    try {
                        runBlocking {
                            val token = try {
                                firebase.currentUser!!.getIdToken(forceRefresh = true)
                            } catch (e: Exception) {
                                firebase.signOut()
                            }
                            header("Authorization", "Bearer $token")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw Exception("Error getting token")
                    }
                }
            }

//            install(HttpRequestRetry) {
//                retryOnServerErrors(maxRetries = 5)
//                exponentialDelay()
//            }

            install(Resources)

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = false
                })
            }
        }
    }
}