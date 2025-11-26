package data.appconfig

import kotlinx.coroutines.flow.Flow


interface ConfigStore {
    suspend fun get(key: String): String?
    fun observe(key: String): Flow<String?>
    suspend fun set(key: String, value: String)
}

interface SyncableConfigStore : ConfigStore {
    override suspend fun get(key: String): String? = get(key, syncable = false)
    override fun observe(key: String): Flow<String?> = observe(key, syncable = false)
    override suspend fun set(key: String, value: String) = set(key, value, syncable = false)

    suspend fun get(key: String, syncable: Boolean = false): String?
    fun observe(key: String, syncable: Boolean = false): Flow<String?>
    suspend fun set(key: String, value: String, syncable: Boolean = false)
}