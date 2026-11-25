package app.moviedb.genre.data.mapper

import app.moviedb.core.data.network.dto.GenreDto
import app.moviedb.genre.data.network.GenreListDto
import app.moviedb.genre.model.Genre

fun GenreDto.toGenreOrNull(): Genre? =
    name.trim().takeIf { it.isNotEmpty() }?.let { Genre(id = id, name = it) }

fun GenreListDto.toGenres(): List<Genre> = genres.mapNotNull { it.toGenreOrNull() }
