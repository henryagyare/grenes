package ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import grenes.composeapp.generated.resources.Res
import me.ayitinya.grenes.data.badge.Badge
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.users.UserId
import moe.tlaster.precompose.koin.koinViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf
import ui.catalog.ErrorScreen
import ui.catalog.FeedCard
import ui.catalog.Loading
import ui.catalog.ProfilePhoto
import ui.screens.State

@Composable
internal fun UserScreen(
    uid: UserId,
    modifier: Modifier = Modifier,
    viewModel: ProfileScreenViewModel = koinViewModel(ProfileScreenViewModel::class) {
        parametersOf(
            uid
        )
    },
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState.user) {
        is State.Loading -> Loading(modifier = modifier)

        is State.Success -> UserScreen(
            modifier = modifier,
            isOwnUser = uiState.isOwnUser,
            username = state.value.username ?: "",
            displayName = state.value.displayName ?: "",
            posts = uiState.feed,
            badges = uiState.badges
        )

        is State.Error -> ErrorScreen(modifier = modifier)
        is State.Empty -> {
            // TODO()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
internal fun UserScreen(
    modifier: Modifier,
    isOwnUser: Boolean,
    badges: State<List<Badge>>,
    username: String,
    displayName: String,
    posts: State<List<Feed>>,
) {
    Scaffold(topBar = {
        TopAppBar(title = { }, actions = {
            if (isOwnUser) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile"
                    )
                }
            }

            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Profile"
                )
            }
        })
    }, modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfilePhoto(displayName = displayName)

                Column {
                    Text(text = displayName, style = MaterialTheme.typography.headlineSmall)
                    if (username.isNotEmpty()) {
                        Text(
                            text = "@$username",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Task,
                        contentDescription = "Challenges",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(text = "500", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Challenges", style = MaterialTheme.typography.bodySmall)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Score,
                        contentDescription = "Points Earned",
                        modifier = Modifier.size(36.dp)

                    )
                    Text(text = "500", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Points Earned", style = MaterialTheme.typography.bodySmall)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.fire_solid),
                        contentDescription = "Streak",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(text = "500", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Days Streak", style = MaterialTheme.typography.bodySmall)
                }
            }

            var state by remember { mutableStateOf(0) }
            val titles = listOf("Post", "Badges", "Gallery")

            TabRow(
                selectedTabIndex = state,
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = { state = index },
                        text = { Text(title) }
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                when (state) {
                    0 -> {
                        when (posts) {
                            is State.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
                            is State.Loading -> Loading(modifier = Modifier.fillMaxSize())
                            is State.Empty -> {}
                            is State.Success -> {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(posts.value) {
                                        FeedCard(
                                            feed = it,
                                            modifier = Modifier.fillMaxWidth(),
                                            navigateToChallenge = {},
                                            reactToFeed = {},
                                            removeReaction = {})
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        LazyColumn {
                            when (badges) {
                                is State.Success -> {
                                    items(badges.value) {
                                        ListItem(
                                            headlineContent = { Text(text = it.name) },
                                            supportingContent = { Text(text = it.description) },
                                            leadingContent = {
                                                Icon(
                                                    painter = painterResource(Res.drawable.feather_award),
                                                    contentDescription = "Badge",
                                                    modifier = Modifier.size(36.dp)
                                                )
                                            },
                                            trailingContent = { Text(if (it.isAchieved) "1 of 1" else "0 of 1") }
                                        )
                                    }
                                }

                                is State.Empty -> item {
                                    Text(text = "No badges yet")

                                }

                                is State.Error -> item {
                                    ErrorScreen(modifier = Modifier.fillMaxSize())
                                }

                                is State.Loading -> item {
                                    Loading(modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    }

                    2 -> {
                        Text(text = "Tab 3")
                    }
                }
            }
        }
    }
}
