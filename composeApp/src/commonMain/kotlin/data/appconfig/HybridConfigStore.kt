package data.appconfig

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class HybridConfigStore(
    private val local: ConfigStore,
    private val remote: ConfigStore?
) : SyncableConfigStore {

    // If cached locally, use the cache. Otherwise fallback to remote
    override suspend fun get(key: String, syncable: Boolean): String? {
        val cached = local.get(key)
        if (cached != null) return cached
        if (!syncable) return null

        val remote = remote?.get(key)
        if (remote != null) {
            local.set(key, remote)
            return remote
        }

        return null
    }

    override fun observe(key: String, syncable: Boolean): Flow<String?> =
        channelFlow {
            val localJob = launch {
                local.observe(key).collect { send(it) }
            }

            val remoteJob = remote?.takeIf { syncable }?.let { store ->
                launch {
                    store.observe(key).collect { remoteValue ->
                        if (remoteValue != null && remoteValue != local.get(key)) {
                            local.set(key, remoteValue)
                        }
                    }
                }
            }

            awaitClose {
                localJob.cancel()
                remoteJob?.cancel()
            }
        }

    // Set both local cache and remote
    override suspend fun set(key: String, value: String, syncable: Boolean) {
        local.set(key, value)
        if (!syncable) return
        remote?.set(key, value)
    }

}
