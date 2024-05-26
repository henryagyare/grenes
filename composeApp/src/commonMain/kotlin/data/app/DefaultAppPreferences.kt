package data.app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DefaultAppPreferences(private val dataStore: DataStore<Preferences>) : AppPreferences {
    private companion object {
        private val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_user_profile_complete")
    }

    override suspend fun getIsOnboardingComplete(): Boolean {
        return dataStore.data.map { it[IS_ONBOARDING_COMPLETE] }.first() ?: false
    }

    override suspend fun setIsOnBoardingComplete(isOnboardingComplete: Boolean) {
        dataStore.edit { it[IS_ONBOARDING_COMPLETE] = isOnboardingComplete }
    }
}