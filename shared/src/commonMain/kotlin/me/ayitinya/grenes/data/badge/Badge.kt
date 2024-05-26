package me.ayitinya.grenes.data.badge

import kotlinx.serialization.Serializable

@Serializable
data class Badge(
    val name: String,
    val description: String,
    val imageUrl: String,
    val uid: String,
    val isAchieved: Boolean = false,
)


val BADGES: List<Badge> = listOf(
    Badge(
        "Familiar Territory",
        "Badge is awarded to users who have made their first post",
        "https://firebasestorage.googleapis.com/v0/b/grenes-1e3e7.appspot.com/o/badges%2Ffirst_aid.png?alt=media&token=3e3e7",
        "familiar_territory"
    ),
)