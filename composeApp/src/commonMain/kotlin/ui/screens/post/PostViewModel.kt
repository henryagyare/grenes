package ui.screens.post

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.name
import com.mohamedrejeb.calf.io.path
import com.mohamedrejeb.calf.io.readByteArray
import data.challenges.ChallengesRepository
import data.feed.FeedRepository
import data.users.UsersRepository
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.ayitinya.grenes.data.feed.FeedDto
import me.ayitinya.grenes.data.media.MediaDto
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class PostViewModel(
    private val usersRepository: UsersRepository,
    private val challengesRepository: ChallengesRepository,
    private val feedRepository: FeedRepository,
    private val challengeId: String? = null,
) : ViewModel() {
    private val _uiState: MutableStateFlow<PostState> = MutableStateFlow(PostState())
    internal val uiState: StateFlow<PostState> = _uiState.asStateFlow()

    init {
        if (challengeId != null) challengeId.let {
            viewModelScope.launch {
                val challenge = challengesRepository.getChallenge(it)
                _uiState.update { it.copy(challenge = challenge, loading = false) }
            }
        } else {
            _uiState.update { it.copy(loading = false) }
        }
    }

    fun onTextContentChange(text: String) {
        _uiState.update { it.copy(textContent = text) }
        validateForm()
    }

    fun onMediaContentChange(media: List<KmpFile>?) {
        _uiState.update {
            it.copy(
                mediaContent = media?.toMutableStateList() ?: mutableStateListOf()
            )
        }
        validateForm()
    }

    fun onPublish() {
        viewModelScope.launch {
            _uiState.update { it.copy(formState = FormState.Submitting) }
            val feedDto = FeedDto(challenge = challengeId,
                content = uiState.value.textContent,
                user = usersRepository.getUser()?.uid?.value ?: "",
                media = uiState.value.mediaContent.map {
                    MediaDto(
                        type = ContentType.Image.Any,
                        bytes = it.readByteArray(),
                        fileName = it.name
                    )
                })
            try {
                feedRepository.create(feedDto).let {
                    println("submitted $it")
                    _uiState.update { state -> state.copy(formState = FormState.Submitted(it.id)) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        formState = FormState.Error(
                            e.message ?: "An Unknown Error Occurred"
                        )
                    )
                }
            }
        }
    }

    private fun validateForm() {
        _uiState.update {
            it.copy(formState = if (_uiState.value.textContent.isNotBlank() || _uiState.value.mediaContent.isNotEmpty()) FormState.Valid else FormState.Invalid)
        }
    }
}