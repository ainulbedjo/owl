package app.moviedb.explore.data.network

import app.moviedb.core.data.network.dto.MovieListDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ExploreApi {

    /** `GET /3/discover/movie`. A `null` query value is left out by Retrofit. */
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("page") page: Int,
        @Query("with_genres") genreId: Int? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("vote_count.gte") minVoteCount: Int? = null,
        @Query("primary_release_date.lte") releasedBefore: String? = null,
    ): MovieListDto
}
