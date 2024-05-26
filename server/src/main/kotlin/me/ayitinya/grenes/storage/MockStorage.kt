package me.ayitinya.grenes.storage

import io.ktor.http.*

class MockStorage : Storage {
    override suspend fun upload(fileName: String, contentType: ContentType?, bytes: ByteArray): String {
        println("Uploading $fileName with content type $contentType and size ${bytes.size} bytes")
        return "https://picsum.photos/id/237/900/500"
    }

    override suspend fun download(url: String): ByteArray {
        println("Downloading $url")
        return byteArrayOf()
    }
}