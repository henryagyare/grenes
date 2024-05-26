package data.app

interface AppPreferences {
    suspend fun getIsOnboardingComplete(): Boolean
    suspend fun setIsOnBoardingComplete(isOnboardingComplete: Boolean)
}