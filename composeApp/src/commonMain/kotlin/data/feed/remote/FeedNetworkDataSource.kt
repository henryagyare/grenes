package data.feed.remote

import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedComment
import me.ayitinya.grenes.data.feed.FeedCommentDto
import me.ayitinya.grenes.data.feed.FeedDto
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.feed.Reaction
import me.ayitinya.grenes.data.feed.ReactionId
import me.ayitinya.grenes.data.users.UserId

interface FeedNetworkDataSource {
    suspend fun create(feed: FeedDto): Feed

    suspend fun read(feedId: FeedId): Feed?

    suspend fun read(nextPageNumber: Int?): List<Feed>

    suspend fun read(userId: UserId): List<Feed>

    suspend fun reactToFeed(feedId: FeedId, userId: UserId): Reaction

    suspend fun removeReaction(reactionId: ReactionId)

    suspend fun comment(comment: FeedCommentDto): FeedComment
}