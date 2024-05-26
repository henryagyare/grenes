package me.ayitinya.grenes.routing

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.ayitinya.grenes.auth.firebase.FIREBASE_AUTH
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedCommentDto
import me.ayitinya.grenes.data.feed.FeedDto
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.feed.FeedService
import me.ayitinya.grenes.data.feed.ReactionType
import me.ayitinya.grenes.data.media.MediaDto
import me.ayitinya.grenes.data.users.UserId
import org.koin.ktor.ext.inject

fun Route.feedRoutes() {
    val feedService by inject<FeedService>()

    authenticate(FIREBASE_AUTH) {
        get<FeedResource> {
            try {
                val feeds = feedService.read(
                    userId = if (!it.userId.isNullOrEmpty()) UserId(it.userId!!) else null,
                    nextPageNumber = it.nextPageNumber
                )
                call.respond(feeds)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    e.message ?: "Internal server error"
                )
            }
        }

        get<FeedResource.FeedDetailResource> { resource ->
            val feed = try {
                feedService.read(FeedId(resource.id))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, "Feed not found")
                return@get
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    e.message ?: "Internal server error"
                )
                return@get
            }
            call.respond(feed)
        }

        post<FeedResource>() {
            try {
                val multiPartData = call.receiveMultipart()

                val media: MutableList<MediaDto> = emptyList<MediaDto>().toMutableList()
                var feedDto = FeedDto(content = "", user = "")

                multiPartData.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val fileBytes = part.streamProvider().readBytes()
                            val mediaDto = MediaDto(
                                type = part.contentType ?: ContentType.Any,
                                bytes = fileBytes,
                                fileName = part.originalFileName ?: part.name
                            )
                            media.add(mediaDto)
                        }

                        is PartData.FormItem -> {
                            when (part.name) {
                                "content" -> feedDto = feedDto.copy(content = part.value)
                                "user" -> feedDto = feedDto.copy(user = part.value)
                                "challenge" -> feedDto = feedDto.copy(challenge = part.value)
                            }
                        }

                        else -> {}
                    }
                }

                feedDto = feedDto.copy(media = media)
                val feed = feedService.create(feedDto)

                call.respond(HttpStatusCode.Created, feed)

            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "Invalid request")
            }
        }

        get<FeedResource.FeedDetailResource.Reaction.Add>() {
            try {
                val response = feedService.createFeedReaction(
                    feedId = it.feedId,
                    userId = it.userId,
                    reactionType = ReactionType.LIKE
                )
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Throwable) {
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    e.message ?: "Internal Server Error"
                )
            }
        }

        delete<FeedResource.FeedDetailResource.Reaction.Delete> {

            try {
                feedService.deleteReaction(it.reactionId)

                call.respond(HttpStatusCode.OK)
            } catch (e: Throwable) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<FeedResource.Comment>() {
            try {
                val comment = call.receive<FeedCommentDto>()

                val response = feedService.createFeedComment(comment)

                call.respond(HttpStatusCode.Created, response)

            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
            } catch (e: Throwable) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}