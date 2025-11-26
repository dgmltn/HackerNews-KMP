package data.appconfig

interface SyncAccountVerifier {
    suspend fun sendVerification(email: String)
    suspend fun confirmVerification(email: String, token: String): Boolean
}