package ui.screens.user

import me.ayitinya.grenes.data.badge.Badge
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.users.User
import ui.screens.State


internal data class ProfileScreenState(
    val isOwnUser: Boolean = false,
    val isFollowing: Boolean = false,
    val user: State<User> = State.Loading(),
    val feed: State<List<Feed>> = State.Loading(),
    val badges: State<List<Badge>> = State.Loading(),
)