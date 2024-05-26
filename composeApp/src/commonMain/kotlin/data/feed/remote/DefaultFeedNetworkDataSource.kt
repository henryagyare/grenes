package data.feed.remote

import dev.gitlive.firebase.storage.FirebaseStorage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.errors.IOException
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedComment
import me.ayitinya.grenes.data.feed.FeedCommentDto
import me.ayitinya.grenes.data.feed.FeedDto
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.feed.Reaction
import me.ayitinya.grenes.data.feed.ReactionId
import me.ayitinya.grenes.data.users.UserId
import me.ayitinya.grenes.routing.FeedResource

class DefaultFeedNetworkDataSource(private val httpClient: HttpClient) : FeedNetworkDataSource {
    override suspend fun create(feed: FeedDto): Feed {
        return try {
            httpClient.post(FeedResource()) {
                setBody(MultiPartFormDataContent(formData {
                    append("content", feed.content)
                    append("user", feed.user)
                    feed.challenge?.let { append("challenge", it) }
                    feed.media.forEach {
                        append("media", it.bytes, headers = Headers.build {
                            append(HttpHeaders.ContentType, it.type)
                            append(HttpHeaders.ContentDisposition, "filename=${it.fileName}")
                        })
                    }
                }))

                onUpload { bytesSentTotal, contentLength ->
                    println("Sent $bytesSentTotal bytes out of $contentLength")
                }
            }.body<Feed>()

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun read(feedId: FeedId): Feed? {
        return try {
            val response = httpClient.get(FeedResource.FeedDetailResource(feedId.value))
            if (response.status == HttpStatusCode.NotFound) return null
            response.body<Feed>()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun read(nextPageNumber: Int?): List<Feed> {
        try {
            val response = httpClient.get(FeedResource(nextPageNumber = nextPageNumber))

            when (response.status) {
                HttpStatusCode.InternalServerError -> throw Exception("Server error")
            }

            return response.body<List<Feed>>()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun read(userId: UserId): List<Feed> {
        return try {
            val resource = FeedResource(userId = userId.value)
            val response = httpClient.get(resource)

            when (response.status) {
                HttpStatusCode.NotFound -> throw Exception("User not found")
                HttpStatusCode.InternalServerError -> throw Exception("Server error")
            }

            val feeds = response.body<List<Feed>>()
            println("feeds = $feeds")
            feeds
//            response.body<List<Feed>>()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun reactToFeed(feedId: FeedId, userId: UserId): Reaction {
        return try {
            val response = httpClient.get(
                FeedResource.FeedDetailResource.Reaction.Add(
                    userId = userId.value,
                    feedId = feedId.value
                )
            )
            when (response.status) {
                HttpStatusCode.NotFound -> throw Exception("User not found")
                HttpStatusCode.InternalServerError -> throw Exception("Server error")
            }

            response.body<Reaction>()
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeReaction(reactionId: ReactionId) {
        try {
            httpClient.delete(
                FeedResource.FeedDetailResource.Reaction.Delete(
                    reactionId = reactionId.value
                )
            )
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun comment(comment: FeedCommentDto): FeedComment {
        try {
            val response = httpClient.post(
                FeedResource.Comment(comment.feedId.value)
            ) {
                setBody(comment)
            }

            when (response.status) {
                HttpStatusCode.NotFound -> throw Exception("User not found")
                HttpStatusCode.InternalServerError -> throw Exception("Server error")
            }

            return response.body<FeedComment>()

        } catch (e: IOException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }
}