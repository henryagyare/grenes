package ui.screens.feeddetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import data.auth.AuthState
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.http.Url
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedComment
import me.ayitinya.grenes.data.feed.FeedId
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.koin.koinViewModel
import navigation.SharedViewModel
import org.koin.core.parameter.parametersOf
import ui.catalog.ErrorScreen
import ui.catalog.FeedCard
import ui.catalog.Loading
import ui.catalog.ProfilePhoto

@Composable
fun FeedDetail(
    feedId: FeedId,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel = koinViewModel(SharedViewModel::class),
    viewModel: FeedDetailsViewModel = koinViewModel(FeedDetailsViewModel::class) {
        parametersOf(
            feedId,
            (sharedViewModel.uiState.value.authState as AuthState.Authenticated).user
        )
    },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (val feedState = uiState.value.feedState) {
        is FeedState.Error -> ErrorScreen(modifier = modifier)
        FeedState.Loading -> Loading(modifier = modifier)
        is FeedState.Ready -> FeedDetail(
            modifier = modifier,
            feed = feedState.feed,
            onNavigateUp = onNavigateUp,
            isOwnUser = uiState.value.isOwnUser,
            userAvatar = { ProfilePhoto(displayName = feedState.feed.user.displayName ?: "") },
            onComment = viewModel::comment
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetail(
    modifier: Modifier,
    feed: Feed,
    isOwnUser: Boolean,
    userAvatar: @Composable () -> Unit,
    onComment: (String) -> Unit,
    onNavigateUp: () -> Unit,
) {
    Scaffold(modifier = modifier, topBar = {
        TopAppBar(title = { }, navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        }, actions = {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share")
            }
            when (isOwnUser) {
                true -> IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                }

                false -> IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "More")
                }
            }
        })
    }, bottomBar = {
        var comment by remember { mutableStateOf("") }

        TextField(value = comment,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { comment = it },
            placeholder = { Text("Leave a comment for ${feed.user.displayName}") },
            leadingIcon = userAvatar,
            trailingIcon = {
                IconButton(onClick = { onComment(comment) }, enabled = comment.isNotEmpty()) {
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = "Send")
                }
            })
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                FeedCard(
                    feed = feed,
                    modifier = Modifier.fillMaxWidth(),
                    navigateToChallenge = {}, reactToFeed = {}, removeReaction = {})
            }

            item { Divider() }

            items(feed.comments) {
                CommentCard(modifier = Modifier.fillMaxWidth(), comment = it)
            }

        }
    }
}

@Composable
fun CommentCard(modifier: Modifier = Modifier, comment: FeedComment) {
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            ProfilePhoto(displayName = comment.user.displayName ?: "")

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = comment.user.displayName ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val remaining = comment.createdAt.periodUntil(
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
                    //                    Text(
                    //                        text = "u/${feed.user.username}",
                    //                        style = MaterialTheme.typography.bodySmall
                    //                    )
                }
            }
        }

        Text(comment.content, modifier = Modifier.padding(horizontal = 8.dp))
    }
}