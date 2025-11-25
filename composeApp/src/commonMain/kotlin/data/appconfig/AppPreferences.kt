package data.appconfig

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class AppPreferences(
    private val configStore: ConfigStore,
    private val json: Json,
) {
    private val serializer = SetSerializer(String.serializer())

    fun observeSeenItems(): Flow<Set<String>> =
        configStore.observe(SEEN_ITEMS_KEY).map { decodeItems(it) }

    suspend fun markItemAsSeen(itemId: String) {
        val currentSeenItems = readSeenItems()
        if (itemId in currentSeenItems) return
        val updatedItems = currentSeenItems + itemId
        configStore.set(SEEN_ITEMS_KEY, encodeItems(updatedItems))
    }

    private suspend fun readSeenItems(): Set<String> =
        decodeItems(configStore.get(SEEN_ITEMS_KEY))

    private fun encodeItems(items: Set<String>): String =
        json.encodeToString(serializer, items)

    private fun decodeItems(raw: String?): Set<String> =
        raw?.let { runCatching { json.decodeFromString(serializer, it) }.getOrElse { emptySet() } } ?: emptySet()

    companion object {
        private const val SEEN_ITEMS_KEY = "seen_items"
    }
}
