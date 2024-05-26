package ui.screens.home

import app.cash.paging.cachedIn
import data.feed.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.feed.Reaction
import me.ayitinya.grenes.data.feed.ReactionId
import me.ayitinya.grenes.data.users.UserId
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ui.screens.State

class HomeViewModel(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    internal val state: StateFlow<HomeState> = _state.asStateFlow()
    internal val feeds = feedRepository.read().cachedIn(viewModelScope)

    suspend fun reactToFeed(feedId: FeedId, userId: UserId, index: Int): Reaction {
        return feedRepository.react(feedId = feedId, userId = userId)
    }

    suspend fun removeReaction(reactionId: ReactionId) {
        feedRepository.removeReaction(reactionId)
    }
}

internal data class HomeState(
    val feeds: State<List<Feed>> = State.Loading(),
)