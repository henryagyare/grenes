package me.ayitinya.grenes.data.media

import io.ktor.http.*
import kotlinx.serialization.Serializable

enum class FileTypes {
    IMAGE,
    VIDEO,
    AUDIO
}

@Serializable
data class Media(
    val id: String,
    val url: String,
    val type: String,
)

/*
* Not serialized as it's not needed in the client
* Data is received as multipart form data
 */
data class MediaDto(
    val type: ContentType,
    val bytes: ByteArray,
    val fileName: String? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MediaDto

        if (type != other.type) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}