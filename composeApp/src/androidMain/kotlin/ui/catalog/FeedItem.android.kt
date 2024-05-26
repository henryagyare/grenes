package ui.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId

@Composable
@Preview
fun FeedCardPreview() {
    val user = User(
        uid = UserId(""),
        profileAvatar = "",
        displayName = "",
        createdAt = Clock.System.now(),
        email = ""
    )
    val feed = Feed(
        id = FeedId(""),
        user = user,
        createdAt = Clock.System.now(),
        media = emptyList(),
        content = "",
        reactions = 0L,
        shares = 2L,
        comments = emptyList(),
    )

//    FeedCard(feed = feed, navigateToChallenge = {})
}