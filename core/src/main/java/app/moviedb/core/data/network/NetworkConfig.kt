package app.moviedb.core.data.network

import kotlinx.serialization.json.Json

const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

// Plain Json throws on the first field TMDB sends that the DTO doesn’t declare.
val networkJson = Json { ignoreUnknownKeys = true }