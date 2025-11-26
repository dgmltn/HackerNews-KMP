package modules

import data.appconfig.AppPreferences
import data.appconfig.ConfigStore
import data.appconfig.HybridConfigStore
import data.appconfig.LocalConfigStore
import data.appconfig.SupabaseAccountVerifier
import data.appconfig.SupabaseConfigStore
import data.appconfig.SyncAccountVerifier
import data.appconfig.SyncableConfigStore
import data.remote.ApiHandler
import domain.models.Ask
import domain.models.Comment
import domain.models.Item
import domain.models.Job
import domain.models.Poll
import domain.models.PollOption
import domain.models.Story
import getPlatform
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.dsl.module

val dataModule = module {
    single {
        val module = SerializersModule {
            polymorphic(Item::class) {
                subclass(Ask::class, Ask.serializer())
                subclass(Comment::class, Comment.serializer())
                subclass(Job::class, Job.serializer())
                subclass(Poll::class, Poll.serializer())
                subclass(PollOption::class, PollOption.serializer())
                subclass(Story::class, Story.serializer())
            }
        }
        Json {
            serializersModule = module
            ignoreUnknownKeys = true
            classDiscriminator = "kind" // Because "type" is a named field in the HN api
        }
    }
    single {
        HttpClient {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.i(message)
                    }
                }
            }
        }.also { Napier.base(DebugAntilog()) }
    }
    single { ApiHandler }
    
    single { getPlatform().createDataStore() }

    single { LocalConfigStore(get()) }

    single { SupabaseConfigStore(get()) }
    single<SyncAccountVerifier> { SupabaseAccountVerifier(get()) }

    single<SyncableConfigStore> { HybridConfigStore(get<LocalConfigStore>(), get<SupabaseConfigStore>()) }
//    single<ConfigStore> { HybridConfigStore(get<LocalConfigStore>(), null) }
//    single<ConfigStore> { LocalConfigStore(get()) }
//    single<ConfigStore> { SupabaseConfigStore() }

    single { AppPreferences(get(), get(), get()) }

    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = getPlatform().supabaseUrl,
            supabaseKey = getPlatform().supabaseKey
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }
}
