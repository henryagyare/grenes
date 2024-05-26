package me.ayitinya.grenes.data.feed

import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.data.challenges.ChallengeDao
import me.ayitinya.grenes.data.media.MediaTable
import me.ayitinya.grenes.data.media.toMedia
import me.ayitinya.grenes.data.users.UserId
import me.ayitinya.grenes.data.users.UsersTable
import me.ayitinya.grenes.data.users.toUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class DefaultFeedDao(private val challengeDao: ChallengeDao) : FeedDao {
    override suspend fun create(content: String, userUid: String, challengeId: String?): Feed {
        return Db.query {
            val challengeUUID = challengeId?.let {
                UUID.fromString(challengeId)
                    ?: throw IllegalArgumentException("Invalid challenge id")
            }
            val challenge = challengeUUID?.let {
                challengeDao.read(challengeUUID)
                    ?: throw IllegalArgumentException("Challenge not found")
            }
            val user = try {
                UsersTable.select { UsersTable.uid eq userUid }.single().toUser()
            } catch (e: NoSuchElementException) {
                throw IllegalArgumentException("User not found")
            }

            val insertStatement = FeedsTable.insert {
                it[FeedsTable.content] = content
                it[FeedsTable.user] = userUid
                it[FeedsTable.challenge] = challengeUUID
            }

            insertStatement.resultedValues?.first()
                ?.toFeed(user = user, challenge = challenge, media = emptyList())
                ?: throw IllegalStateException("Post not created")

        }
    }

    override suspend fun getFeed(feedId: FeedId): Feed {
        return Db.query {
            val feed = FeedsTable.select { FeedsTable.id eq UUID.fromString(feedId.value) }
                .orderBy(column = FeedsTable.createdAt, order = SortOrder.DESC).first()

            val media =
                MediaTable.select { MediaTable.feed eq feed[FeedsTable.id] }.map { it.toMedia() }
            val user = UsersTable.select { UsersTable.uid eq feed[FeedsTable.user] }.firstOrNull()
                ?.toUser()
            val reactions =
                ReactionsTable.select { ReactionsTable.reactionTo eq feed[FeedsTable.id].toString() }
                    .map {
                        val reactionUser =
                            UsersTable.select { UsersTable.uid eq it[ReactionsTable.user] }.first()
                                .toUser()
                        it.toReaction(user = reactionUser)
                    }
            val comments = FeedComments.select { FeedComments.feed eq feed[FeedsTable.id] }.map {

                val commentUser =
                    UsersTable.select { UsersTable.uid eq it[FeedComments.user] }.first().toUser()
                it.toFeedComment(
                    feed = feedId,
                    reactions = emptyList(),
                    user = commentUser
                )
            }

            val shares = 0L

            return@query feed.toFeed(
                reactions = reactions,
                comments = comments,
                shares = shares,
                user = user!!,
                media = media
            )
        }
    }

    override suspend fun getFeeds(
        userId: UserId?,
        nextPageNumber: Int?,
        pageSize: Int?,
    ): List<Feed> {
        return Db.query {
            val feedsQuery = FeedsTable.selectAll()
                .orderBy(column = FeedsTable.createdAt, order = SortOrder.DESC)

            userId?.let { feedsQuery.andWhere { FeedsTable.user eq it.value } }

            nextPageNumber?.let {
                if (pageSize != null) {
                    feedsQuery.limit(n = pageSize, offset = ((it - 1) * pageSize).toLong())
                }
            }

            val feeds = feedsQuery.toList()

            return@query feeds.map { feed ->
                val media =
                    MediaTable.select { MediaTable.feed eq feed[FeedsTable.id] }
                        .map { it.toMedia() }
                val user =
                    UsersTable.select { UsersTable.uid eq feed[FeedsTable.user] }.firstOrNull()
                        ?.toUser()
                val reactions =
                    ReactionsTable.select { ReactionsTable.reactionTo eq feed[FeedsTable.id].toString() }
                        .map {
                            val reactionUser =
                                UsersTable.select { UsersTable.uid eq it[ReactionsTable.user] }
                                    .first().toUser()
                            it.toReaction(user = reactionUser)
                        }
                val comments =
                    FeedComments.select { FeedComments.feed eq feed[FeedsTable.id] }.map {

                        val commentUser =
                            UsersTable.select { UsersTable.uid eq it[FeedComments.user] }.first()
                                .toUser()
                        it.toFeedComment(
                            feed = FeedId(""),
                            reactions = emptyList(),
                            user = commentUser
                        )
                    }

                val shares = 0L

                return@map feed.toFeed(
                    reactions = reactions,
                    comments = comments,
                    shares = shares,
                    user = user!!,
                    media = media
                )
            }
        }
    }

    override suspend fun updateFeed(feedId: String, feed: FeedDto) {
//        return Db.query {
//            FeedsTable.update({ FeedsTable.id eq UUID.fromString(feedId) }) {
//                it[content] = feed.content
//                it[user] = feed.user
//                it[challenge] = UUID.fromString(feed.challenge)
//            }
//
//            feed.media.forEach { media ->
//                val mediaUUID = UUID.fromString(media)
//                FeedMedia.insertIgnore {
//                    it[FeedMedia.feed] = UUID.fromString(feedId)
//                    it[FeedMedia.media] = mediaUUID
//                }
//            }
//
//
//        }
    }

    override suspend fun deleteFeed(feedId: String) {
        return Db.query {
            FeedsTable.deleteWhere { FeedsTable.id eq UUID.fromString(feedId) }
        }
    }

    override suspend fun getFeedComments(feedId: String): List<FeedComment> {
        return Db.query {
            val feed = FeedsTable.select { FeedsTable.id eq UUID.fromString(feedId) }.firstOrNull()
            if (feed == null) {
                return@query emptyList()
            }

            val comments = FeedComments.select { FeedComments.feed eq feed[FeedsTable.id] }.toList()
            return@query comments.map { comment ->
                val user =
                    UsersTable.select { UsersTable.uid eq comment[FeedComments.user] }.first()
                        .toUser()
                val reactions = ReactionsTable.join(
                    UsersTable,
                    JoinType.INNER,
                    additionalConstraint = { ReactionsTable.user eq UsersTable.uid })
                    .select { ReactionsTable.reactionTo eq comment[FeedComments.id].toString() }
                    .map {
                        val user = it.toUser()
                        val reaction = it.toReaction(user)
                        reaction
                    }

                return@map comment.toFeedComment(
                    feed = FeedId(""),
                    reactions = reactions,
                    user = user
                )
            }
        }
    }

    override suspend fun createFeedComment(
        feedComment: FeedCommentDto,
    ): FeedComment {
        return Db.query {
            val insertFeedCommentStatement = FeedComments.insert {
                it[content] = feedComment.content
                it[user] = feedComment.userId.value
                it[FeedComments.feed] = UUID.fromString(feedComment.feedId.value)
            }

            val feed =
                FeedsTable.select { FeedsTable.id eq UUID.fromString(feedComment.feedId.value) }
                    .firstOrNull()
            if (feed == null) {
                throw IllegalArgumentException("Feed not found")
            }

            val user =
                UsersTable.select { UsersTable.uid eq feedComment.userId.value }.first()
                    .toUser()

            val reactions = ReactionsTable.select {
                ReactionsTable.reactionTo eq insertFeedCommentStatement[FeedComments.id].toString()
            }.count()

            return@query insertFeedCommentStatement.resultedValues?.first()?.toFeedComment(
                feed = FeedId(""),
                reactions = emptyList(),
                user = user
            )!!
        }
    }

    override suspend fun updateFeedComment(
        feedId: String,
        feedCommentId: String,
        feedComment: FeedCommentCreation,
    ): FeedComment? {
        return Db.query {
            FeedComments.update({ FeedComments.id eq UUID.fromString(feedCommentId) }) {
                it[content] = feedComment.content
                it[user] = feedComment.user.uid.value
            }

            val feed = FeedsTable.select { FeedsTable.id eq UUID.fromString(feedId) }.firstOrNull()
            if (feed == null) {
                return@query null
            }

            val user =
                UsersTable.select { UsersTable.uid eq feedComment.user.uid.value }.first()
                    .toUser()
            val reactions =
                ReactionsTable.select { ReactionsTable.reactionTo eq feed[FeedsTable.id].toString() }
                    .map {
                        val reactionUser =
                            UsersTable.select { UsersTable.uid eq it[ReactionsTable.user] }.first()
                                .toUser()
                        it.toReaction(user = reactionUser)
                    }

            return@query FeedComments.select { FeedComments.id eq UUID.fromString(feedCommentId) }
                .firstOrNull()
                ?.toFeedComment(
                    feed = FeedId(""),
                    reactions = emptyList(),
                    user = user
                )
        }
    }

    override suspend fun deleteFeedComment(feedCommentId: String) {
        return Db.query {
            FeedComments.deleteWhere { FeedComments.id eq UUID.fromString(feedCommentId) }
        }
    }

    override suspend fun getFeedReactions(feedId: String): List<Reaction> {
        return Db.query {
            val feed = FeedsTable.select { FeedsTable.id eq UUID.fromString(feedId) }.firstOrNull()
            if (feed == null) {
                return@query emptyList()
            }

            return@query ReactionsTable.join(
                UsersTable,
                JoinType.INNER,
                additionalConstraint = { ReactionsTable.user eq UsersTable.uid })
                .select { ReactionsTable.reactionTo eq feed[FeedsTable.id].toString() }
                .map {
                    val user = it.toUser()
                    val reaction = it.toReaction(user)
                    reaction
                }
        }
    }

    override suspend fun createFeedReaction(
        feedId: String,
        reactionType: ReactionType,
        userId: String,
    ): Reaction {
        return Db.query {
            val insertReactionStatement = ReactionsTable.insert {
                it[user] = userId
                it[reactable] = Reactables.FEED
                it[ReactionsTable.reactionType] = reactionType
                it[reactionTo] = feedId
            }

            val user = UsersTable.select { UsersTable.uid eq userId }.firstOrNull()?.toUser()

            return@query insertReactionStatement.resultedValues?.first()?.toReaction(user!!)!!
        }
    }

    override suspend fun deleteReaction(reactionId: String) {
        return Db.query {
            ReactionsTable.deleteWhere { ReactionsTable.id eq UUID.fromString(reactionId) }
        }
    }

    override suspend fun getFeedCommentReactions(feedCommentId: String): List<Reaction> {
        return Db.query {
            return@query ReactionsTable.join(
                UsersTable,
                JoinType.INNER,
                additionalConstraint = { ReactionsTable.user eq UsersTable.uid })
                .select { ReactionsTable.reactionTo eq feedCommentId }
                .map {
                    val user = it.toUser()
                    val reaction = it.toReaction(user)
                    reaction
                }
        }
    }

    override suspend fun createFeedCommentReaction(
        feedCommentId: String,
        userId: String,
    ): Reaction {
        return Db.query {
            val insertReactionStatement = ReactionsTable.insert {
                it[user] = userId
                it[reactable] = Reactables.COMMENT
                it[reactionTo] = feedCommentId
            }

            val user = UsersTable.select { UsersTable.uid eq userId }.firstOrNull()?.toUser()

            return@query insertReactionStatement.resultedValues?.first()?.toReaction(user!!)!!
        }
    }
}