package me.ayitinya.grenes.data.users

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.ayitinya.grenes.data.badge.Badge
import kotlin.jvm.JvmInline

@Serializable
data class User(
    val uid: UserId,
    val username: String? = null,
    val displayName: String? = null,
    val email: String,
    val createdAt: Instant,
    val profileAvatar: String? = null,
    val city: String? = null,
    val country: String? = null,
    val badges: List<Badge> = emptyList()
)

@Serializable
@JvmInline
value class UserId(val value: String)