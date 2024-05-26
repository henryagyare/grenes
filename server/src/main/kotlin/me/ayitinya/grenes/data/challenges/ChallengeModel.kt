package me.ayitinya.grenes.data.challenges

import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UsersTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp


/*
* The initial idea is to make this an enum class
* with daily, weekly, monthly, yearly and none as the values
* but this can be extended to include custom timelines
* hence the decision to make it a table
* */
internal object ChallengeTypes : UUIDTable() {
    val name = varchar("name", 255)

    init {
        uniqueIndex(name)
    }
}


internal object Challenges : UUIDTable() {
    val title = varchar("title", 255)
    val description = text("description", eagerLoading = true)
    val createdAt = timestamp("createdAt").clientDefault { Clock.System.now() }
    val suggestedBy = optReference(
        "suggestedBy", UsersTable.uid, onDelete = ReferenceOption.NO_ACTION, onUpdate = ReferenceOption.NO_ACTION
    )
    val startDate = timestamp("startDate").nullable()
    val endDate = timestamp("endDate").nullable()

    val isActive = bool("isActive").default(false)
    val isTrackable = bool("isTrackable").default(false)
    val difficulty = enumerationByName("difficulty", 10, ChallengeDifficulty::class).default(ChallengeDifficulty.EASY)
}

internal object ChallengeTypeChallenges : UUIDTable() {
    val challengeType = reference(
        "challengeTypes", ChallengeTypes.id, onDelete = ReferenceOption.NO_ACTION, onUpdate = ReferenceOption.NO_ACTION
    )
    val challenge = reference(
        "challenge", Challenges.id, onDelete = ReferenceOption.NO_ACTION, onUpdate = ReferenceOption.NO_ACTION
    )

    init {
        uniqueIndex(challengeType, challenge)
    }
}

internal fun ResultRow.toChallengeType(): ChallengeType {
    return ChallengeType(
        name = this[ChallengeTypes.name], uid = this[ChallengeTypes.id].toString()
    )
}


fun ResultRow.toChallenge(
    suggestedBy: User? = null,
    challengeTypes: List<ChallengeType>,
): Challenge {
    return Challenge(
        title = this[Challenges.title],
        description = this[Challenges.description],
        suggestedBy = suggestedBy,
        createdAt = this[Challenges.createdAt],
        uid = this[Challenges.id].toString(),
        challengeTypes = challengeTypes,
        startAt = this[Challenges.startDate],
        endAt = this[Challenges.endDate],
        isActive = this[Challenges.isActive],
        isTrackable = this[Challenges.isTrackable],
        difficulty = this[Challenges.difficulty]
    )
}