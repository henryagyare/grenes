package me.ayitinya.grenes.data.location

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: String,
    val city: String,
    val country: String,
)