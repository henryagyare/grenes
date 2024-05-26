package ui.screens.challengedetails

import data.challenges.ChallengesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.ayitinya.grenes.data.challenges.Challenge
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ChallengeDetailViewModel(private val uid: String, private val challengesRepository: ChallengesRepository) :
    ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(challenge = null))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val challenge = challengesRepository.getChallenge(uid)
            _uiState.update { state -> state.copy(challenge = challenge, loading = false) }
        }
    }
}

data class UiState(
    val challenge: Challenge?,
    val loading: Boolean = true,
)