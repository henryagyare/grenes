package me.ayitinya.grenes.data.feed

import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.challenges.Challenges
import me.ayitinya.grenes.data.media.Media
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UsersTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp


internal object FeedsTable : UUIDTable() {
    val content = largeText("content", eagerLoading = true)
    val editedAt = timestamp("editedAt").nullable()
    val createdAt = timestamp("createdAt").clientDefault { Clock.System.now() }
    val user =
        reference(
            "user",
            UsersTable.uid,
            onDelete = ReferenceOption.NO_ACTION,
            onUpdate = ReferenceOption.NO_ACTION
        )
    val challenge = optReference(
        "challenge",
        Challenges,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION
    )
}

internal object FeedComments : UUIDTable() {
    val feed = reference("feed", FeedsTable)
    val user = reference("user", UsersTable.uid)
    val content = mediumText("content", eagerLoading = true)
    val createdAt = timestamp("createdAt").clientDefault { Clock.System.now() }
}

internal object ReactionsTable : UUIDTable() {
    val user = reference("user", UsersTable.uid)
    val reactable = enumerationByName<Reactables>("reactable", 10)
    val reactionType =
        enumerationByName<ReactionType>("reactionType", 10).clientDefault { ReactionType.LIKE }
    val reactionTo = varchar("reactionTo", 255)

    init {
        uniqueIndex(user, reactable, reactionTo)
    }
}

fun ResultRow.toFeed(
    reactions: List<Reaction> = emptyList(),
    comments: List<FeedComment> = emptyList(),
    shares: Long = 0,
    user: User,
    media: List<Media>,
    challenge: Challenge? = null,
): Feed {
    return Feed(
        id = FeedId(this[FeedsTable.id].toString()),
        content = this[FeedsTable.content],
        createdAt = this[FeedsTable.createdAt],
        reactionsList = reactions.toMutableList(),
        reactions = 0L,
        comments = comments,
        shares = shares,
        user = user,
        media = media,
        challenge = challenge
    )
}

fun ResultRow.toFeedComment(feed: FeedId, user: User, reactions: List<Reaction>): FeedComment {
    return FeedComment(
        feedId = feed,
        content = this[FeedComments.content],
        createdAt = this[FeedComments.createdAt],
        user = user,
        reactions = reactions
    )
}

fun ResultRow.toReaction(user: User): Reaction {
    return Reaction(
        reactionId = ReactionId(this[ReactionsTable.id].toString()),
        reactionType = this[ReactionsTable.reactionType],
        user = user
    )
}