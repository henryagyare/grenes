package ui.catalog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.feed.Reaction
import me.ayitinya.grenes.data.feed.ReactionId
import me.ayitinya.grenes.data.feed.ReactionType
import me.ayitinya.grenes.data.users.User
import me.ayitinya.grenes.data.users.UserId
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import kotlin.jvm.JvmInline

sealed class UserReactedState {
    data object NotReacted : UserReactedState()
    data class Reacted(val reaction: Reaction) : UserReactedState()
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedCard(
    feed: Feed,
    modifier: Modifier = Modifier,
    onClick: ((feedId: FeedId) -> Unit)? = null,
    navigateToChallenge: (id: String) -> Unit,
    reactToFeed: (feedId: FeedId) -> Unit,
    removeReaction: (reactionId: ReactionId) -> Unit,
    userHasReacted: UserReactedState = UserReactedState.NotReacted,
) {
    val scope = rememberCoroutineScope()
    var reacted by remember { mutableStateOf(userHasReacted) }
    var reactionCount by remember { mutableIntStateOf(feed.reactionsList.size) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        onClick = {
            if (onClick != null) {
                onClick(feed.id)
            }
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            ProfilePhoto(displayName = feed.user.displayName ?: "")

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = feed.user.displayName ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val remaining = feed.createdAt.periodUntil(
                        Clock.System.now(),
                        TimeZone.currentSystemDefault()
                    )
                    val time = when {
                        remaining.years > 0 -> remaining.years.toString() + "y"
                        remaining.months > 0 -> remaining.months.toString() + "m"
                        remaining.days > 0 -> remaining.days.toString() + "d"
                        remaining.hours > 0 -> remaining.hours.toString() + "h"
                        remaining.minutes > 0 -> remaining.minutes.toString() + "m"
                        else -> remaining.seconds.toString() + "s"
                    } + " ago"

                    Text(text = time, style = MaterialTheme.typography.bodySmall)
                    feed.challenge?.let {
                        Text(text = " • ", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clickable {
                                navigateToChallenge(it.uid)
                            })
                    }
                    //                    Text(
                    //                        text = "u/${feed.user.username}",
                    //                        style = MaterialTheme.typography.bodySmall
                    //                    )
                }
            }
        }

        Text(
            text = feed.content,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        if (feed.media.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                val pagerState =
                    rememberPagerState(
                        initialPage = 0,
                        initialPageOffsetFraction = 0f,
                        pageCount = feed.media::size
                    )

                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                ) { page ->
                    val media = feed.media[page]

                    KamelImage(
                        resource = asyncPainterResource(data = Url(media.url)),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                        contentScale = ContentScale.FillWidth,
                        onLoading = {
                            CircularProgressIndicator(
                                modifier = Modifier.fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        },
                        onFailure = {
                            Box(
                                modifier = Modifier.fillMaxSize()
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "An error occurred while loading image",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        }
                    )
                }

                if ((pagerState.pageCount - pagerState.currentPage) != pagerState.pageCount) {
                    Icon(
                        modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(100))
                            .align(Alignment.CenterStart)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage - 1
                                    )
                                }
                            },
                        tint = MaterialTheme.colorScheme.onPrimary,
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = "Go back"
                    )
                }

                if ((pagerState.pageCount - pagerState.currentPage) != 1) {
                    Icon(
                        modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(100))
                            .align(Alignment.CenterEnd)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage + 1
                                    )
                                }
                            },
                        tint = MaterialTheme.colorScheme.onPrimary,
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Go forward"
                    )
                }
            }
        }



        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Forum,
                        contentDescription = "Reply"
                    )
                }

                IconButton(onClick = {
                    if (reacted is UserReactedState.Reacted) {
                        reactionCount--
                        removeReaction((reacted as UserReactedState.Reacted).reaction.reactionId)
                        reacted = UserReactedState.NotReacted
                    } else {
                        reactToFeed(feed.id)

                        reacted = UserReactedState.Reacted(
                            Reaction(
                                reactionType = ReactionType.LIKE,
                                user = User(
                                    uid = UserId("1"),
                                    email = "",
                                    createdAt = Clock.System.now()
                                ),
                                reactionId = ReactionId("")
                            )
                        )

                        reactionCount++
                    }
                }) {
                    Icon(
                        imageVector = if (reacted is UserReactedState.Reacted) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Give A Star"
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            if (reactionCount > 0) {
                Text(
                    "$reactionCount reaction" + if (reactionCount > 1) "s" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.typography.bodySmall.color.copy(alpha = 0.8f)
                )
            }
        }
    }
}