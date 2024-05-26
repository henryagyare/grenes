package me.ayitinya.grenes.data.media

import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.users.UserId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import java.util.*

class DefaultMediaDao : MediaDao {
    override suspend fun insert(fileUrl: String, types: String, uid: UserId, feedId: FeedId) {
        try {
            Db.query {
                MediaTable.insert {
                    it[MediaTable.fileUrl] = fileUrl
                    it[MediaTable.fileType] = types
                    it[MediaTable.user] = uid.value
                    it[MediaTable.feed] = UUID.fromString(feedId.value)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun delete(id: UUID) {
        return Db.query {
            MediaTable.deleteWhere { MediaTable.id eq id }
        }
    }

//    override suspend fun getMediaByUserId(uid: String): List<Media> = Db.query {
//        return@query Media.find { MediaTable.user eq uid }.toList()
//    }
//
//    override suspend fun getMediaByUserIdAndType(uid: String, type: FileTypes): List<Media> =
//        Db.query {
//            return@query Media.find {(MediaTable.user eq uid) and (MediaTable.type eq type)}.toList()
//        }
//
//    override suspend fun getMediaById(id: UUID): Media? = Db.query {
//        return@query Media.findById(id)
//    }
}