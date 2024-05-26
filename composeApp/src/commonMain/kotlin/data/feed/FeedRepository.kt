package data.feed

import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedComment
import me.ayitinya.grenes.data.feed.FeedCommentDto
import me.ayitinya.grenes.data.feed.FeedDto
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.feed.Reaction
import me.ayitinya.grenes.data.feed.ReactionId
import me.ayitinya.grenes.data.users.UserId

interface FeedRepository {

    suspend fun create(feed: FeedDto): Feed

    suspend fun read(feedId: FeedId): Feed?

    fun read(): Flow<PagingData<Feed>>

    suspend fun read(userId: UserId): List<Feed>

    suspend fun react(feedId: FeedId, userId: UserId): Reaction

    suspend fun removeReaction(reactionId: ReactionId)

    suspend fun comment(comment: FeedCommentDto): FeedComment
}