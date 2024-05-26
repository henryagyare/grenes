package ui.screens.challenges

import data.challenges.ChallengesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import me.ayitinya.grenes.data.badge.Badge
import me.ayitinya.grenes.data.challenges.Challenge
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ChallengesViewModel(private val challengesRepository: ChallengesRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<ChallengeUiState> = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState

    init {
        viewModelScope.launch {
            val challenges = challengesRepository.getChallenges()

            _uiState.update { state ->
                state.copy(monthlyChallenges = challenges.filter { challenge ->
                    challenge.challengeTypes.any {
                        it.name.equals(
                            "monthly", ignoreCase = true
                        )
                    }
                }, dailyChallenges = challenges.filter { challenge ->
                    challenge.challengeTypes.any {
                        it.name.equals(
                            "daily", ignoreCase = true
                        )
                    }
                }, loading = false
                )
            }

            println("Challenges: ${uiState.value}")
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun calculateTimeRemaining(): Duration {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val midnight = LocalDateTime(now.date, LocalTime(23, 59, 59))
        return now.toInstant(TimeZone.currentSystemDefault())
            .until(midnight.toInstant(TimeZone.currentSystemDefault()), DateTimeUnit.SECOND)
            .toDuration(DurationUnit.HOURS)
    }

}

data class ChallengeUiState(
    val selectedTabIndex: Int = 0,
    val monthlyChallenges: List<Challenge> = emptyList(),
    val dailyChallenges: List<Challenge> = emptyList(),
    val dailyChallengeTimeRemaining: String = "12 Hours",
    val loading: Boolean = true,
)