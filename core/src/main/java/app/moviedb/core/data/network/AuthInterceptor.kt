package app.moviedb.core.data.network

import okhttp3.Interceptor
import okhttp3.Response

// Adds the TMDB read access token to every request.
class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .header("accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}