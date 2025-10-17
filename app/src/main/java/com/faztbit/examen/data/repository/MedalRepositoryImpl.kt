package com.faztbit.examen.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.faztbit.examen.data.datastore.DefaultMedals
import com.faztbit.examen.data.datastore.MedalPreferences
import com.faztbit.examen.domain.entity.Medal
import com.faztbit.examen.domain.repository.MedalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : MedalRepository {

    private val json: Json = Json { ignoreUnknownKeys = true }

    override suspend fun getMedals(): Flow<List<Medal>> {
        return dataStore.data.map { preferences ->
            val medalDataJson: String? = preferences[MedalPreferences.MEDAL_DATA_KEY]
            if (medalDataJson.isNullOrEmpty()) {
                initializeDefaultMedals()
                DefaultMedals.DEFAULT_MEDALS
            } else {
                try {
                    json.decodeFromString<List<Medal>>(medalDataJson)
                } catch (e: Exception) {
                    DefaultMedals.DEFAULT_MEDALS
                }
            }
        }
    }

    override suspend fun updateMedal(medal: Medal) {
        val currentMedals: MutableList<Medal> = getMedals().first().toMutableList()
        val index: Int = currentMedals.indexOfFirst { it.id == medal.id }
        if (index != -1) {
            currentMedals[index] = medal
            saveMedals(currentMedals)
        }
    }

    override suspend fun resetAllMedals() {
        saveMedals(DefaultMedals.DEFAULT_MEDALS)
    }

    override suspend fun initializeDefaultMedals() {
        val currentData: Preferences = dataStore.data.first()
        if (currentData[MedalPreferences.MEDAL_DATA_KEY].isNullOrEmpty()) {
            saveMedals(DefaultMedals.DEFAULT_MEDALS)
        }
    }

    override suspend fun getAvatarTapCount(): Int {
        return dataStore.data.first()[MedalPreferences.AVATAR_TAP_COUNT_KEY] ?: 0
    }

    override suspend fun updateAvatarTapCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[MedalPreferences.AVATAR_TAP_COUNT_KEY] = count
        }
    }

    override suspend fun getLastTapTime(): Long {
        return dataStore.data.first()[MedalPreferences.LAST_TAP_TIME_KEY] ?: 0L
    }

    override suspend fun updateLastTapTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[MedalPreferences.LAST_TAP_TIME_KEY] = time
        }
    }

    override suspend fun getEngineState(): Boolean {
        return dataStore.data.first()[MedalPreferences.ENGINE_STATE_KEY] ?: false
    }

    override suspend fun setEngineState(isRunning: Boolean) {
        dataStore.edit { preferences ->
            preferences[MedalPreferences.ENGINE_STATE_KEY] = isRunning
        }
    }

    private suspend fun saveMedals(medals: List<Medal>) {
        dataStore.edit { preferences ->
            preferences[MedalPreferences.MEDAL_DATA_KEY] = json.encodeToString(medals)
        }
    }
}