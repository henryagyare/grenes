package me.ayitinya.grenes.data.feed

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.feed.Reactables.COMMENT
import me.ayitinya.grenes.data.feed.Reactables.FEED
import me.ayitinya.grenes.data.media.Media
import me.ayitinya.grenes.data.media.MediaDto
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId
import kotlin.jvm.JvmInline

/**
 * Can be a feed or a comment
 *
 * @property FEED
 * @property COMMENT
 */
enum class Reactables {
    FEED, COMMENT
}

/**
 * The different types of reactions
 * user can have on a reactable
 *
 * @property ReactionType
 * @property user
 */
enum class ReactionType {
    LIKE, LOVE, FUNNY, LAUGH
}

@Serializable
@JvmInline
value class FeedId(val value: String)

@Serializable
data class Feed(
    val id: FeedId,
    val content: String,
    val createdAt: Instant,
    val editedAt: Instant? = null,
    val challenge: Challenge? = null,
    val reactionsList: MutableList<Reaction> = emptyList<Reaction>().toMutableList(),
    val reactions: Long,
    val comments: List<FeedComment> = emptyList(),
    val shares: Long,
    val user: User,
    val media: List<Media> = emptyList(),
)

/*
* Not serialized as it's not needed in the client
* Data is received as multipart form data
 */
data class FeedDto(
    val content: String,
    val user: String,
    val challenge: String? = null,
    val media: List<MediaDto> = emptyList(),
)

@Serializable
data class FeedCommentDto(
    val feedId: FeedId,
    val content: String,
    val userId: UserId,
)

@Serializable
data class FeedComment(
    val feedId: FeedId,
    val content: String,
    val createdAt: Instant,
    val user: User,
    val reactions: List<Reaction> = emptyList(),
)

@Serializable
data class FeedCommentCreation(
    val content: String,
    val user: User,
)

@JvmInline
@Serializable
value class ReactionId(val value: String)

@Serializable
data class Reaction(
    val reactionId: ReactionId,
    val reactionType: ReactionType,
    val user: User,
)