package app.moviedb.core.data.network.service

import android.content.Context
import app.moviedb.core.BuildConfig
import app.moviedb.core.data.network.dto.MovieDetailDto
import app.moviedb.core.data.network.dto.MovieListDto
import app.moviedb.core.data.network.dto.ReviewListDto
import com.chuckerteam.chucker.api.ChuckerInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): MovieListDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): MovieListDto

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): MovieListDto

    @GET("movie/{id}")
    suspend fun getMovieDetail(@Path("id") id: Int): MovieDetailDto

    @GET("movie/{id}/reviews")
    suspend fun getReviews(@Path("id") id: Int, @Query("page") page: Int = 1): ReviewListDto
}