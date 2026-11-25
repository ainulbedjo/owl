package app.moviedb.explore.data.mapper

import app.moviedb.core.data.mapper.toMovie
import app.moviedb.core.data.network.dto.MovieListDto
import app.moviedb.explore.model.MoviePage

fun MovieListDto.toMoviePage(): MoviePage = MoviePage(
    items = results.map { it.toMovie() },
    page = page,
    totalPages = totalPages,
    totalResults = totalResults,
)
