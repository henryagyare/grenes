package me.ayitinya.grenes

import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId
import java.util.UUID

val usersList = listOf(
    User(
        uid = UserId(UUID.randomUUID().toString()),
        displayName = "Test User",
        email = "william.henry.moody@my-own-personal-domain.com",
        createdAt = Clock.System.now(),
        profileAvatar = null
    ), User(
        uid = UserId(UUID.randomUUID().toString()),
        displayName = "Test User",
        email = "william.howard.taft@my-own-personal-domain.com",
        createdAt = Clock.System.now(),
        profileAvatar = null
    )
)