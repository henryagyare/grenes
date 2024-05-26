package ui.screens.onboarding.profile

import data.app.AppPreferences
import data.auth.AuthRepository
import data.users.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class OnboardingProfileScreenViewModel(
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileScreenState> =
        MutableStateFlow(ProfileScreenState(displayName = "", city = "", country = ""))
    val uiState: StateFlow<ProfileScreenState> = _uiState

    fun updateDisplayName(displayName: String) {
        _uiState.value = _uiState.value.copy(displayName = displayName)
    }

    fun updateCity(city: String) {
        _uiState.value = _uiState.value.copy(city = city)
    }

    fun updateCountry(country: String) {
        _uiState.value = _uiState.value.copy(country = country)
    }

    fun createUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.getCurrentUser().take(1).collect { firebaseUser ->
                if (firebaseUser != null) {
                    usersRepository.createProfile(
                        firebaseUser.uid,
                        firebaseUser.email ?: "",
                        firebaseUser.photoURL,
                        _uiState.value.displayName,
                        _uiState.value.city,
                        _uiState.value.country
                    )
                }
                appPreferences.setIsOnBoardingComplete(true)
            }
        }
    }
}