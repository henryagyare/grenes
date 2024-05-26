package me.ayitinya.grenes.storage

import com.google.cloud.storage.BlobId
import com.google.firebase.cloud.StorageClient
import io.ktor.http.*

class FirebaseStorage : Storage {
    private val bucket = StorageClient.getInstance().bucket()

    override suspend fun upload(fileName: String, contentType: ContentType?, bytes: ByteArray): String {

        val blob = bucket.create(fileName, bytes, contentType?.contentType ?: ContentType.Any.contentType)
        return blob.mediaLink
    }

    override suspend fun download(url: String): ByteArray {
        val blobId = BlobId.of(bucket.name, url)
        return bucket.storage.get(blobId).getContent()
    }
}