package ui.screens.user

import data.auth.AuthState
import data.feed.FeedRepository
import data.users.UsersRepository
import domain.AuthenticationUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ayitinya.grenes.data.badge.BADGES
import me.ayitinya.grenes.data.users.UserId
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ui.screens.State

internal class ProfileScreenViewModel(
    private val uid: UserId,
    private val usersRepository: UsersRepository,
    private val feedRepository: FeedRepository,
    private val authenticationUseCase: AuthenticationUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileScreenState> = MutableStateFlow(
        ProfileScreenState()
    )

    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                authenticationUseCase.getAuthState().take(1).collect { authState ->
                    if (authState is AuthState.Authenticated) {
                        if (authState.user.uid.value == uid.value) {
                            _uiState.update {
                                it.copy(isOwnUser = true)
                            }
                        }
                        getUserInfo()
                    }
                }
            }

            launch {
                feedRepository.read(userId = uid).let { challenges ->
                    _uiState.update {
                        it.copy(feed = State.Success(challenges))
                    }
                }
            }
        }
    }

    private suspend fun getUserInfo() {
        if (uiState.value.isOwnUser) {
            try {
                usersRepository.getUser().let { user ->
                    if (user != null) {
                        viewModelScope.launch {
                            launch {
                                _uiState.update {
                                    it.copy(user = State.Success(user))
                                }
                            }
                            launch {
                                val badges = BADGES.map { badge ->
                                    badge.copy(
                                        isAchieved = user.badges.any { it.uid == badge.uid }
                                    )
                                }

                                _uiState.update {
                                    it.copy(badges = State.Success(badges))
                                }
                            }

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(user = State.Error(e.message))
                }
            }
        } else {
            try {
                usersRepository.getUser(uid).let { user ->
                    if (user != null) {
                        _uiState.update {
                            it.copy(user = State.Success(user))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(user = State.Error(e.message))
                }
            }

        }
    }
}