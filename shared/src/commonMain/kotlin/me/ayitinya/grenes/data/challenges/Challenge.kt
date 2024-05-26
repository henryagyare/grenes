package me.ayitinya.grenes.data.challenges

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.ayitinya.grenes.data.users.User

/*
* Track this issue: https://youtrack.jetbrains.com/issue/KT-11914/Confusing-data-class-copy-with-private-constructor
* Idea is to use a builder pattern to create the challenge object
* But it beats the purpose of using it in the first place
* As I was trying to avoid using nulls in the constructor,
* Accessors would assume the fields are not null
* And NPEs would be thrown
 */

enum class ChallengeDifficulty(val value: String) {
    EASY("EASY"), MEDIUM("MEDIUM"), HARD("HARD")
}

@Serializable
data class ChallengeCreation(
    val title: String,
    val description: String,
    val suggestedBy: String? = null,
    val challengeTypes: List<ChallengeType> = emptyList(),
    val startAt: String? = null,
    val endAt: String? = null,
)

@Serializable
data class ChallengeType(val name: String, val uid: String)

@Serializable
data class Challenge(
    val title: String,
    val description: String,
    val suggestedBy: User? = null,
    val createdAt: Instant,
    val uid: String,
    val challengeTypes: List<ChallengeType>,
    val startAt: Instant? = null,
    val endAt: Instant? = null,
    val isActive: Boolean,
    val isTrackable: Boolean,
    val difficulty: ChallengeDifficulty
)
