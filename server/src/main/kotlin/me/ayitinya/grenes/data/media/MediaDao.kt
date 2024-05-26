package me.ayitinya.grenes.data.media

import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.users.UserId
import java.util.UUID

interface MediaDao {
    suspend fun insert(fileUrl: String, types: String, uid: UserId, feedId: FeedId)

    suspend fun delete(id: UUID)

//    suspend fun getMediaByUserId(uid: String): List<Media>
//
//    suspend fun getMediaByUserIdAndType(uid: String, type: FileTypes): List<Media>
//
//    suspend fun getMediaById(id: UUID): Media?
}