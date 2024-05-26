package me.ayitinya.grenes.data.feed

import me.ayitinya.grenes.data.users.UserId

interface FeedDao {
    suspend fun create(content: String, userUid: String, challengeId: String? = null): Feed
    suspend fun getFeed(feedId: FeedId): Feed
    suspend fun getFeeds(userId: UserId?, nextPageNumber: Int?, pageSize: Int? = 10): List<Feed>
    suspend fun updateFeed(feedId: String, feed: FeedDto)
    suspend fun deleteFeed(feedId: String)
    suspend fun getFeedComments(feedId: String): List<FeedComment>
    suspend fun createFeedComment(feedComment: FeedCommentDto): FeedComment
    suspend fun updateFeedComment(feedId: String, feedCommentId: String, feedComment: FeedCommentCreation): FeedComment?
    suspend fun deleteFeedComment(feedCommentId: String)
    suspend fun getFeedReactions(feedId: String): List<Reaction>
    suspend fun createFeedReaction(feedId: String, reactionType: ReactionType, userId: String): Reaction
    suspend fun deleteReaction(reactionId: String)
    suspend fun getFeedCommentReactions(feedCommentId: String): List<Reaction>
    suspend fun createFeedCommentReaction(feedCommentId: String, userId: String): Reaction
}