package data.appconfig

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseConfigStore(private val supabase: SupabaseClient) : ConfigStore {

    private suspend fun ensureClient(): SupabaseClient? {
        if (supabase.auth.currentSessionOrNull() != null) return supabase

        //TODO: sign in??? Or maybe it's already signed in

        if (supabase.auth.currentSessionOrNull() != null) return supabase

        return null
    }

    private suspend fun ensureUserId(): String? =
        ensureClient()
            ?.auth
            ?.currentUserOrNull()?.id

    override suspend fun get(key: String): String? =
        ensureClient()
            ?.from(TABLE_NAME)
            ?.select {
                filter {
                    UserConfigRow::key eq key
                }
            }
            ?.decodeSingleOrNull<UserConfigRow>()
            ?.value

    @OptIn(SupabaseExperimental::class)
    override fun observe(key: String): Flow<String?> =
        channelFlow {
            val job = launch {
                ensureClient()
                    ?.from(TABLE_NAME)
                    ?.selectSingleValueAsFlow(
                        primaryKey = UserConfigRow::key,
                        filter = { eq("key", key) }
                    )
                    ?.collect { send(it.value) }
            }

            awaitClose {
                job.cancel()
            }
        }

    override suspend fun set(key: String, value: String) {
        val userId = ensureUserId() ?: return
        val client = ensureClient() ?: return

        client
            .from(TABLE_NAME)
            .upsert(
                value = UserConfigRow(
                    userId = userId,
                    key = key,
                    value = value,
                )
            )
    }

    companion object {

        @Serializable
        private data class UserConfigRow(
            @SerialName("user_id")
            val userId: String,
            val key: String,
            val value: String,
            @SerialName("updated_at")
            val updatedAt: String? = null,
        )

        private const val TABLE_NAME = "user_configs"

    }
}
