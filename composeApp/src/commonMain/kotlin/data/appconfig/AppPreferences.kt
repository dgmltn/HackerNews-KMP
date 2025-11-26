package data.appconfig

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class AppPreferences(
    private val configStore: SyncableConfigStore,
    private val accountVerifier: SyncAccountVerifier,
    private val json: Json,
) {
    private val serializer = SetSerializer(String.serializer())

    ///////////////////////////////////////////////////////////////////////////
    // region private, non-synced prefs

    fun observeIsSyncEnabled(): Flow<Boolean> =
        configStore.observe(KEY_IS_SYNC_ENABLED, syncable = false).map { it == "true" }

    suspend fun setIsSyncEnabled(enabled: Boolean) {
        configStore.set(KEY_IS_SYNC_ENABLED, enabled.toString(), syncable = false)
    }

    suspend fun getSyncEmailAddress(): String? =
        configStore.get(KEY_SYNC_EMAIL_ADDRESS, syncable = false)

    fun observeSyncEmailAddress(): Flow<String?> =
        configStore.observe(KEY_SYNC_EMAIL_ADDRESS, syncable = false)

    suspend fun setSyncEmailAddress(emailAddress: String) {
        if (emailAddress == getSyncEmailAddress()) return
        configStore.set(KEY_SYNC_EMAIL_ADDRESS, emailAddress, syncable = false)
        accountVerifier.sendVerification(emailAddress)
    }

    suspend fun confirmSyncEmail(token: String) {
        val emailAddress = configStore.get(KEY_SYNC_EMAIL_ADDRESS) ?: return
        val confirmed = accountVerifier.confirmVerification(emailAddress, token)
        if (confirmed) setIsSyncEmailConfirmed(true)
    }

    fun observeIsSyncEmailConfirmed(): Flow<Boolean> =
        configStore.observe(KEY_IS_SYNC_EMAIL_CONFIRMED, syncable = false).map { it == "true" }

    suspend fun setIsSyncEmailConfirmed(confirmed: Boolean) {
        configStore.set(KEY_IS_SYNC_EMAIL_CONFIRMED, confirmed.toString(), syncable = false)
    }

    // endregion
    ///////////////////////////////////////////////////////////////////////////
    // region synced prefs

    private suspend fun getSeenItems(): Set<String> =
        decodeItems(configStore.get(KEY_SEEN_ITEMS, syncable = true))

    fun observeSeenItems(): Flow<Set<String>> =
        configStore.observe(KEY_SEEN_ITEMS, syncable = true).map { decodeItems(it) }

    suspend fun markItemAsSeen(itemId: String) {
        val currentSeenItems = getSeenItems()
        if (itemId in currentSeenItems) return
        val updatedItems = currentSeenItems + itemId
        configStore.set(KEY_SEEN_ITEMS, encodeItems(updatedItems), syncable = true)
    }

    // endregion
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Convert item id's to a JSON encoded array.
     */
    private fun encodeItems(items: Set<String>): String =
        json.encodeToString(serializer, items)

    /**
     * Convert a JSON encoded array to a set of item id's.
     */
    private fun decodeItems(raw: String?): Set<String> =
        raw?.let { runCatching { json.decodeFromString(serializer, it) }.getOrElse { emptySet() } } ?: emptySet()

    companion object {
        private const val KEY_IS_SYNC_ENABLED = "sync_enabled"
        private const val KEY_IS_SYNC_EMAIL_CONFIRMED = "sync_email_confirmed"
        private const val KEY_SEEN_ITEMS = "seen_items"
        private const val KEY_SYNC_EMAIL_ADDRESS = "sync_email_address"
    }
}
