package me.ayitinya.grenes.storage

import io.ktor.http.*

interface Storage {
    suspend fun upload(fileName: String, contentType: ContentType?, bytes: ByteArray): String
    suspend fun download(url: String): ByteArray
}