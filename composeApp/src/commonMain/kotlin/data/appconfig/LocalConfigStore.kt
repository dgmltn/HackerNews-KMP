package data.appconfig

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class LocalConfigStore(private val dataStore: DataStore<Preferences>) : ConfigStore {
    override suspend fun get(key: String): String? {
        val preferences = dataStore.data.first()
        return preferences.stringOrMaybeStringSet(key)
    }

    override fun observe(key: String): Flow<String?> {
        return dataStore.data.map { preferences -> preferences.stringOrMaybeStringSet(key) }
    }

    override suspend fun set(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    // This is here for legacy purposes. We originally used stringSetPreferenceKey
    private fun Preferences.stringOrMaybeStringSet(key: String): String? {
        try {
            // Try reading as StringSet
            val set = get(stringSetPreferencesKey(key))
            if (set != null) {
                return "$set"
            }
        } catch (_: Exception) {
            // Not a Set<String>
        }

        try {
            // Fallback to String
            return get(stringPreferencesKey(key))
        } catch (_: ClassCastException) {
            // Not a String
        }

        return null
    }

}
