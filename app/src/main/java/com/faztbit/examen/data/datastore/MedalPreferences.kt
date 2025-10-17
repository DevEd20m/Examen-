package com.faztbit.examen.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object MedalPreferences {
    val MEDAL_DATA_KEY = stringPreferencesKey("medal_data")
    val ENGINE_STATE_KEY = booleanPreferencesKey("engine_running")
    val LAST_UPDATE_KEY = longPreferencesKey("last_update_timestamp")
    val AVATAR_TAP_COUNT_KEY = intPreferencesKey("avatar_tap_count")
    val LAST_TAP_TIME_KEY = longPreferencesKey("last_tap_time")
}