package me.ayitinya.grenes.data.media

import io.ktor.http.*
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.users.UserId

interface MediaService {
    suspend fun uploadMedia(
        bytes: ByteArray,
        type: ContentType,
        uid: UserId,
        feedId: FeedId,
        fileName: String,
    )
}