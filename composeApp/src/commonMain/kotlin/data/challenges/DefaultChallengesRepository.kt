package data.challenges

import data.challenges.remote.ChallengeNetworkDataSource
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.challenges.ChallengeCreation
import me.ayitinya.grenes.data.users.UserId

class DefaultChallengesRepository(private val challengeNetworkDataSource: ChallengeNetworkDataSource) :
    ChallengesRepository {
    override suspend fun getChallenges(): List<Challenge> {
        val challenges = challengeNetworkDataSource.getChallenges()

        return challenges
    }

    override suspend fun getUserChallenges(userId: UserId): List<Challenge> =
        challengeNetworkDataSource.getUserChallenges(userId)

    override suspend fun getChallenge(uid: String): Challenge? {
        val challenge = challengeNetworkDataSource.getChallenge(uid)

        return challenge
    }

    override suspend fun createChallenge(challenge: ChallengeCreation): Challenge {
        val createdChallenge = challengeNetworkDataSource.createChallenge(challenge)

        return createdChallenge
    }

    override suspend fun updateChallenge(challenge: Challenge): Challenge {
        val updatedChallenge = challengeNetworkDataSource.updateChallenge(challenge)

        return updatedChallenge
    }

    override suspend fun deleteChallenge(uid: String) {
        challengeNetworkDataSource.deleteChallenge(uid)
    }
}