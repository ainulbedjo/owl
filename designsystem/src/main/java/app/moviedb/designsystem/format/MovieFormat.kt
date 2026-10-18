package app.moviedb.designsystem.format

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

/** Release date for a card, or an empty string when TMDB sent none. */
fun LocalDate?.toCardDate(): String = this?.format(dateFormatter).orEmpty()

/** One decimal, for example `8.4`. */
fun Double.toRatingLabel(): String = "%.1f".format(this)
