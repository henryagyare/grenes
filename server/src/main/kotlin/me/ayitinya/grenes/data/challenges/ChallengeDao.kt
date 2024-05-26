package me.ayitinya.grenes.data.challenges

import kotlinx.datetime.Instant
import me.ayitinya.grenes.data.users.UserId
import java.util.*

interface ChallengeDao {
    suspend fun create(
        title: String,
        description: String,
        suggestedBy: String?,
        challengeTypes: List<UUID>,
        startDate: Instant?,
        endDate: Instant?,
    ): Challenge?

    suspend fun createChallengeType(name: String): ChallengeType?

    suspend fun getAllChallengeTypes(): List<ChallengeType>

    suspend fun deleteChallengeType(uid: UUID): Int

    suspend fun read(uid: UUID): Challenge?

    suspend fun read(
        userId: UserId? = null,
        challengeType: List<String>? = null,
        date: Instant? = null,
        suggestedBy: String? = null,
        isActive: Boolean? = null,
    ): List<Challenge>

    suspend fun update(uid: UUID, challenge: Challenge): Int

    suspend fun delete(uid: UUID): Int
}