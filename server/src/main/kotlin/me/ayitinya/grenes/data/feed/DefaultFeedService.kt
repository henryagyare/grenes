package me.ayitinya.grenes.data.feed

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ayitinya.grenes.data.badge.BADGES
import me.ayitinya.grenes.data.media.MediaService
import me.ayitinya.grenes.data.users.UserDao
import me.ayitinya.grenes.data.users.UserId

class DefaultFeedService(
    private val mediaService: MediaService,
    private val feedDao: FeedDao,
    private val usersDao: UserDao,
) :
    FeedService {
    override suspend fun create(feed: FeedDto): Feed {

        if (feedDao.getFeeds(userId = UserId(feed.user), nextPageNumber = null).isEmpty()) {
            val user = usersDao.getUserById((feed.user))
            if (user == null) {
                throw IllegalArgumentException("User not found")
            } else {
                usersDao.updateUser(
                    uid = user.uid.value,
                    user = user.copy(badges = user.badges.plus(BADGES.find { it.uid == "familiar_territory" }!!))
                )
            }
        }


        return feedDao.create(
            content = feed.content,
            userUid = feed.user,
            challengeId = feed.challenge
        )
            .let { createdFeed ->
                feed.media.forEach {
                    CoroutineScope(Dispatchers.IO).launch {
                        mediaService.uploadMedia(
                            bytes = it.bytes,
                            type = it.type,
                            uid = createdFeed.user.uid,
                            feedId = createdFeed.id,
                            fileName = it.fileName
                                ?: throw IllegalArgumentException("File name is required")
                        )
                    }
                }
                return@let createdFeed
            }
    }

    override suspend fun update(feedId: String, feed: FeedDto) {
        feedDao.updateFeed(feedId, feed)

        feed.media.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                mediaService.uploadMedia(
                    bytes = it.bytes,
                    type = it.type,
                    uid = UserId(feed.user),
                    feedId = FeedId(feedId),
                    fileName = it.fileName
                        ?: throw IllegalArgumentException("File name is required")
                )
            }
        }
    }

    override suspend fun read(feedId: FeedId): Feed = feedDao.getFeed(feedId)

    override suspend fun read(userId: UserId?, nextPageNumber: Int?): List<Feed> =
        feedDao.getFeeds(userId, nextPageNumber = nextPageNumber)

    override suspend fun createFeedReaction(
        feedId: String,
        reactionType: ReactionType,
        userId: String,
    ) = feedDao.createFeedReaction(feedId, reactionType, userId)

    override suspend fun deleteReaction(reactionId: String) = feedDao.deleteReaction(reactionId)
    override suspend fun createFeedComment(comment: FeedCommentDto): FeedComment {
        return feedDao.createFeedComment(comment)
    }
}