package me.ayitinya.grenes.data.feed

import me.ayitinya.grenes.data.users.UserId

interface FeedService {
    suspend fun create(feed: FeedDto): Feed

    suspend fun update(feedId: String, feed: FeedDto)

    suspend fun read(feedId: FeedId): Feed

    suspend fun read(userId: UserId? = null, nextPageNumber: Int? = null): List<Feed>

    suspend fun createFeedReaction(feedId: String, reactionType: ReactionType, userId: String): Reaction

    suspend fun deleteReaction(reactionId: String)

    suspend fun createFeedComment(comment: FeedCommentDto): FeedComment
}