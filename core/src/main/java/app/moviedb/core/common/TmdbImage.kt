package app.moviedb.core.common

// image widths this app requests. Sizes per screen: docs/api/tmdb_overview.md#images.
enum class ImageSize(internal val segment: String) {
    W154("w154"),
    W185("w185"),
    W342("w342"),
    W500("w500"),
    W780("w780"),
}

object TmdbImage {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"

    /** Full URL for a TMDB image [path] such as `/abc.jpg`, or `null` when there is no image. */
    fun url(path: String?, size: ImageSize): String? {
        if (path.isNullOrBlank()) return null
        return BASE_URL + size.segment + path
    }
}
