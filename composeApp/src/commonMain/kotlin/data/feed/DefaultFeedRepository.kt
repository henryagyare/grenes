package data.feed

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import data.feed.remote.FeedNetworkDataSource
import kotlinx.coroutines.flow.Flow
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedComment
import me.ayitinya.grenes.data.feed.FeedCommentDto
import me.ayitinya.grenes.data.feed.FeedDto
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.feed.ReactionId
import me.ayitinya.grenes.data.users.UserId

class DefaultFeedRepository(private val feedNetworkDataSource: FeedNetworkDataSource) :
    FeedRepository {

    override suspend fun create(feed: FeedDto): Feed = feedNetworkDataSource.create(feed)

    override suspend fun read(feedId: FeedId): Feed? = feedNetworkDataSource.read(feedId)

    override fun read(): Flow<PagingData<Feed>> {
        return Pager(
            config = PagingConfig(pageSize = 50, enablePlaceholders = true),
            pagingSourceFactory = { FeedPagingSource(feedNetworkDataSource) }
        ).flow
    }

    override suspend fun read(userId: UserId): List<Feed> = feedNetworkDataSource.read(userId)

    override suspend fun react(feedId: FeedId, userId: UserId) =
        feedNetworkDataSource.reactToFeed(feedId = feedId, userId = userId)

    override suspend fun removeReaction(reactionId: ReactionId) {
        feedNetworkDataSource.removeReaction(reactionId)
    }

    override suspend fun comment(comment: FeedCommentDto) = feedNetworkDataSource.comment(comment)
}