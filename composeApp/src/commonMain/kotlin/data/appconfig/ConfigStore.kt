package data.appconfig

import kotlinx.coroutines.flow.Flow


interface ConfigStore {
    // Use Strings by default
    suspend fun get(key: String): String?
    fun observe(key: String): Flow<String?>
    suspend fun set(key: String, value: String)
}
