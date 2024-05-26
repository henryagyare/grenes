package me.ayitinya.grenes.data.media

import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.feed.FeedsTable
import me.ayitinya.grenes.data.users.UsersTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp


object MediaTable : UUIDTable() {
    val fileUrl = varchar("fileUrl", 255)
    val fileType = varchar("fileType", 255)
    val createdAt = timestamp("createdAt").default(Clock.System.now())
    val user = reference("user", UsersTable.uid)
    val feed = reference("feed", FeedsTable, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.NO_ACTION)
}

fun ResultRow.toMedia() = Media(
    id = this[MediaTable.id].toString(),
    type = this[MediaTable.fileType],
    url = this[MediaTable.fileUrl],
)
