package app.moviedb.genre.data.network

import retrofit2.http.GET

interface GenreApi {

    /** `language` is not sent. TMDB defaults to `en`. */
    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreListDto
}
