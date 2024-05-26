package me.ayitinya.grenes.data.post

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.media.Media
import me.ayitinya.grenes.data.media.MediaDto
import me.ayitinya.grenes.data.users.User

//@Serializable
//data class Post(
//    val id: String,
//    val title: String,
//    val content: String,
//    val author: User,
//    val date: Instant,
//    val editedAt: Instant? = null,
//    val challenge: Challenge? = null,
//    val media: List<Media> = emptyList()
//)

/*
* Not serialized as it's not needed in the client
* Data is received as multipart form data
 */
//data class PostDto(
//    val title: String,
//    val content: String,
//    val author: String,
//    val challenge: String? = null,
//    val media: List<MediaDto> = emptyList()
//)