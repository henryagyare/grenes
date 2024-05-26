package data.challenges

import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.challenges.ChallengeCreation
import me.ayitinya.grenes.data.users.UserId

interface ChallengesRepository {
    suspend fun getChallenges(): List<Challenge>

    suspend fun getUserChallenges(userId: UserId): List<Challenge>

    suspend fun getChallenge(uid: String): Challenge?

    suspend fun createChallenge(challenge: ChallengeCreation): Challenge

    suspend fun updateChallenge(challenge: Challenge): Challenge

    suspend fun deleteChallenge(uid: String)
}