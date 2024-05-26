package ui.screens.feeddetails

import data.feed.FeedRepository
import data.users.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.ayitinya.grenes.data.feed.Feed
import me.ayitinya.grenes.data.feed.FeedCommentDto
import me.ayitinya.grenes.data.feed.FeedId
import me.ayitinya.grenes.data.users.User
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class FeedDetailsViewModel(
    private val feedId: FeedId,
    private val user: User,
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<FeedDetailState> = MutableStateFlow(FeedDetailState())
    val uiState: StateFlow<FeedDetailState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            feedRepository.read(feedId).let {
                println("Feed $it")
                if (it != null) {
//                    val user = usersRepository.getUser()

                    _uiState.update { state ->
                        state.copy(
                            feedState = FeedState.Ready(it),
                            isOwnUser = user.uid == it.user.uid
                        )
                    }

                } else {
                    _uiState.update { state -> state.copy(feedState = FeedState.Error("Feed not found")) }
                }
            }
        }
    }

    fun comment(value: String) {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isCommenting = true) }
            val comment = FeedCommentDto(
                feedId = (uiState.value.feedState as FeedState.Ready).feed.id,
                content = value,
                userId = user.uid
            )

            feedRepository.comment(comment)

            feedRepository.read(feedId).let {
                if (it != null) {
                    _uiState.update { state ->
                        state.copy(
                            feedState = FeedState.Ready(it),
                            isOwnUser = state.isOwnUser
                        )
                    }

                } else {
                    _uiState.update { state -> state.copy(feedState = FeedState.Error("Feed not found")) }
                }
            }
            _uiState.update { state ->
                state.copy(isCommenting = false)
            }
        }
    }
}

data class FeedDetailState(
    val feedState: FeedState = FeedState.Loading,
    val isOwnUser: Boolean = false,
    val isCommenting: Boolean = false,
)

sealed class FeedState {
    data object Loading : FeedState()

    data class Error(val message: String) : FeedState()

    data class Ready(val feed: Feed) : FeedState()
}