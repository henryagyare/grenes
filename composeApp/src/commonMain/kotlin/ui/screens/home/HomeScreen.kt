package ui.screens.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import data.auth.AuthState
import dev.materii.pullrefresh.DragRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.feed.Reaction
import me.ayitinya.grenes.data.feed.ReactionId
import me.ayitinya.grenes.data.feed.ReactionType
import me.ayitinya.grenes.data.users.UserId
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import navigation.SharedViewModel
import ui.catalog.FeedCard
import ui.catalog.UserReactedState

@Composable
fun HomeScreenUi(
    makeSubmission: () -> Unit,
    navigateToFeed: (feedId: FeedId) -> Unit = {},
    modifier: Modifier = Modifier, viewModel: HomeViewModel = koinViewModel(HomeViewModel::class),
    sharedViewModel: SharedViewModel = koinViewModel(
        SharedViewModel::class
    ),
) {
    val feeds = viewModel.feeds.collectAsLazyPagingItems()

    HomeScreenUi(
        onFabClick = makeSubmission,
        navigateToFeed = navigateToFeed,
        modifier = modifier,
        feeds = feeds,
        userId = (sharedViewModel.uiState.value.authState as AuthState.Authenticated).user.uid,
        reactToFeed = { feedId: FeedId, index: Int ->
            viewModel.viewModelScope.launch {
                viewModel.reactToFeed(
                    feedId = feedId,
                    userId = (sharedViewModel.uiState.value.authState as AuthState.Authenticated).user.uid,
                    index = index
                ).let { reaction ->
                    feeds[index]!!.reactionsList.add(reaction)
                }
            }
        }, removeReaction = { reactionId: ReactionId, index: Int ->
            viewModel.viewModelScope.launch {
                viewModel.removeReaction(reactionId)
                try {
                    feeds[index]!!.reactionsList.removeAt(index)
                } catch (e: IndexOutOfBoundsException) {
                    println("Index out of bounds")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenUi(
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier,
    feeds: LazyPagingItems<Feed>,
    navigateToFeed: (feedId: FeedId) -> Unit = { },
    userId: UserId,
    reactToFeed: (feedId: FeedId, index: Int) -> Unit = { _: FeedId, _: Int -> run {} },
    removeReaction: (reactionId: ReactionId, index: Int) -> Unit = { _: ReactionId, _: Int -> run {} },
) {
    Scaffold(modifier = modifier, topBar = {
        TopAppBar(title = { Text(text = "Feed") }, actions = {
            Row {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = onFabClick) {
            Icon(Icons.Default.Edit, null)
        }
    }) { innerPadding ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = feeds.loadState.refresh == LoadState.Loading,
            onRefresh = feeds::refresh
        )

        DragRefreshLayout(
            modifier = modifier.padding(innerPadding).fillMaxSize(),
            state = pullRefreshState
        ) {
            LazyColumn {
                items(feeds.itemCount) {

                    val r = feeds[it]?.reactionsList?.map { reactions -> reactions.user.uid }
                    val uhr = if (r?.contains(userId) == true) {
                        feeds[it]!!.reactionsList[r.indexOf(userId)]
                    } else {
                        null
                    }

                    FeedCard(
                        feed = feeds[it]!!,
                        onClick = navigateToFeed,
                        navigateToChallenge = {},
                        reactToFeed = { feedId -> reactToFeed(feedId, it) },
                        removeReaction = { reactionId -> removeReaction(reactionId, it) },
                        userHasReacted = if (uhr != null) UserReactedState.Reacted(uhr) else UserReactedState.NotReacted,
                    )
                }

                if (feeds.loadState.append == LoadState.Loading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}
