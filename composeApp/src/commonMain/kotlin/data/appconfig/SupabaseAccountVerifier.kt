package data.appconfig

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP

class SupabaseAccountVerifier(private val supabase: SupabaseClient): SyncAccountVerifier {
    override suspend fun sendVerification(email: String) {
        supabase.auth.signInWith(
            provider = OTP,
            redirectUrl = "pulseapp://auth-callback",
            config = {
                this.email = email
            }
        )
    }

    /**
     * Token will probably be a url like "pulseapp://auth-callback#access_token=...&refresh_token=...&type=signup"
     */
    override suspend fun confirmVerification(email: String, token: String): Boolean {
        Napier.e("DOUG: confirmVerification token: $token")
        val accessToken = token
            .substringAfter("#")
            .split("&")
            .firstOrNull { it.startsWith("access_token=") }
            ?.substringAfter("=")
            ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
            ?: return false
        Napier.e("DOUG: email = $email, accessToken = $accessToken")

        supabase.auth.verifyEmailOtp(
            type = OtpType.Email.EMAIL,
            email = email,
            token = accessToken,
        )

        return true
    }
}