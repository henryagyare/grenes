package data.challenges.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.challenges.ChallengeCreation
import me.ayitinya.grenes.data.users.UserId
import me.ayitinya.grenes.routing.ChallengeResource

class DefaultChallengeNetworkDataSource(private val httpClient: HttpClient) : ChallengeNetworkDataSource {
    override suspend fun getChallenges(): List<Challenge> {
        return try {
            val now = Clock.System.now()
            httpClient.get(ChallengeResource(date = now.toString(), isActive = true)).body<List<Challenge>>()
        } catch (exception: Exception) {
            throw exception
        }
    }

    override suspend fun getChallenge(uid: String): Challenge? {
        return try {
            httpClient.get(ChallengeResource.Detail(uid = uid)).body<Challenge?>()
        } catch (exception: Exception) {
            throw exception
        }
    }

    override suspend fun getUserChallenges(userId: UserId): List<Challenge> {
        return try {
            val response = httpClient.get(ChallengeResource(userId = userId))

            if (response.status == HttpStatusCode.NotFound) {
                return emptyList()
            }

            response.body<List<Challenge>>()

        } catch (exception: Exception) {
            throw exception
        }
    }

    override suspend fun createChallenge(challenge: ChallengeCreation): Challenge {
        return try {
            httpClient.post(ChallengeResource.Create()) {
                contentType(ContentType.Application.Json)
                setBody(challenge)
            }.body<Challenge>()
        } catch (exception: Exception) {
            throw exception
        }
    }

    override suspend fun updateChallenge(challenge: Challenge): Challenge {
        return try {
            httpClient.put(ChallengeResource.Detail(uid = challenge.uid)) {
                contentType(ContentType.Application.Json)
                setBody(challenge)
            }.body<Challenge>()
        } catch (exception: Exception) {
            throw exception
        }
    }

    override suspend fun deleteChallenge(uid: String) {
        try {
            httpClient.delete(ChallengeResource.Detail(uid = uid))
        } catch (exception: Exception) {
            throw exception
        }
    }
}