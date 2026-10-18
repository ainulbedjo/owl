package app.moviedb.core.di

import android.content.Context
import app.moviedb.core.BuildConfig
import app.moviedb.core.data.network.AuthInterceptor
import app.moviedb.core.data.network.TMDB_BASE_URL
import app.moviedb.core.data.network.networkJson
import app.moviedb.core.data.network.service.ApiService
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(BuildConfig.ACCESS_TOKEN))

        if (BuildConfig.DEBUG) {
            val chucker = ChuckerInterceptor.Builder(context)
                .maxContentLength(250_000L)
                .alwaysReadResponseBody(true)
                .build()

            builder.addInterceptor(chucker)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}