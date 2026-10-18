# Movie detail

One endpoint feeds the top of the Detail screen. The PDF does not list it, but the screen needs a description, runtime, genres, and a backdrop that the list items do not carry. Common rules are in [tmdb_overview.md](tmdb_overview.md).

| Endpoint | Reference |
|---|---|
| `GET /3/movie/{movie_id}` | https://developer.themoviedb.org/reference/movie-details |

## Why a separate call

**Decision:** navigation passes only the movie id, and Detail fetches the full movie itself. The alternative was to pass the list item along, but that fails on a deep link or after process death, and the list item has no `runtime`, `genres`, or `tagline`.

The Detail screen therefore makes two calls, which load and fail independently:

1. `GET /3/movie/{movie_id}`: this page.
2. `GET /3/movie/{movie_id}/reviews`: see [movie_reviews.md](movie_reviews.md).

## Request

| Parameter | In | Type | Required | Default | Notes |
|---|---|---|---|---|---|
| `movie_id` | path | integer (int32) | yes | none | The movie id from the list item. |
| `language` | query | string | no | `en-US` | Not sent by the app. |
| `append_to_response` | query | string | no | none | Comma-separated endpoints, 20 at most. Not used. |

`append_to_response=reviews` would fold both calls into one. We keep them separate so that a reviews failure does not blank the whole screen.

Retrofit sketch:

```kotlin
@GET("movie/{movie_id}")
suspend fun movieDetail(@Path("movie_id") id: Int): MovieDetailDto
```

## Response (200)

The reference lists these top-level fields:

| Field | Reference type | Used |
|---|---|---|
| `id` | integer | yes |
| `title` | string | yes |
| `tagline` | string | yes |
| `overview` | string | yes |
| `poster_path` | string | yes (share sheet, favorites snapshot) |
| `backdrop_path` | string | yes (header image) |
| `release_date` | string | yes |
| `runtime` | integer | yes (minutes) |
| `genres` | array of `{id, name}` | yes |
| `vote_average` | number | yes |
| `vote_count` | integer | no |
| `adult` | boolean | no |
| `belongs_to_collection` | object | no |
| `budget` | integer | no |
| `homepage` | string | no |
| `imdb_id` | string | no |
| `origin_country` | array of string | no |
| `original_language` | string | no |
| `original_title` | string | no |
| `popularity` | number | no |
| `production_companies` | array of object | no |
| `production_countries` | array of object | no |
| `revenue` | integer | no |
| `spoken_languages` | array of object | no |
| `status` | string | no |
| `video` | boolean | no |

Nested shapes in the reference, for completeness:

- `genres[]`: `id` (integer), `name` (string).
- `belongs_to_collection`: `id`, `name`, `poster_path`, `backdrop_path`.
- `production_companies[]`: `id`, `logo_path`, `name`, `origin_country`.
- `production_countries[]`: `iso_3166_1`, `name`.
- `spoken_languages[]`: `english_name`, `iso_639_1`, `name`.

### DTO

The DTO declares only the fields marked "yes". `ignoreUnknownKeys = true` drops the rest.

```kotlin
@Serializable
data class MovieDetailDto(
    val id: Int,
    val title: String,
    val tagline: String? = null,
    val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    val genres: List<GenreDto> = emptyList(),
    @SerialName("vote_average") val voteAverage: Double = 0.0,
)

@Serializable
data class GenreDto(val id: Int, val name: String)
```

Nullability follows [tmdb_overview.md](tmdb_overview.md#nullability-and-defensive-parsing): the reference marks nothing nullable, but `tagline`, `overview`, `runtime`, and the image paths can be empty or `null` in practice.

## Mapping to the domain model

| `MovieDetail` field | Type | From | Rule |
|---|---|---|---|
| `id` | `Int` | `id` | Copy. |
| `title` | `String` | `title` | Copy. |
| `tagline` | `String?` | `tagline` | Blank becomes `null`, so the UI hides the line. |
| `overview` | `String` | `overview` | `null` becomes `""`. The UI shows "No description available." when blank. |
| `posterPath` | `String?` | `poster_path` | Copy, `null` stays `null`. |
| `backdropPath` | `String?` | `backdrop_path` | Copy, `null` stays `null`. The header falls back to the poster, then to a color placeholder. |
| `releaseDate` | `LocalDate?` | `release_date` | Parse `yyyy-MM-dd`. Empty or invalid becomes `null`. |
| `runtimeMinutes` | `Int?` | `runtime` | `0` or `null` becomes `null`, so the UI hides the chip. |
| `genres` | `List<String>` | `genres[].name` | Names only. |
| `rating` | `Double` | `vote_average` | Copy. |

## Favorites snapshot

When the user taps Favorite, the app stores a snapshot built from this response: `id`, `title`, `poster_path`, `release_date`, `overview`, `vote_average`. The Favorites screen then works offline and needs no API call. Details are in `features/favorites.md` (planned).

The snapshot is taken from the detail response, not the list item, so it is the same whether the user arrived from Home or from Favorites.

## Sample response

Illustrative data with a made-up movie, trimmed to the fields we read.

```json
{
  "id": 900101,
  "title": "Midnight Orchard",
  "tagline": "Every harvest keeps a secret.",
  "overview": "When a night shift at a failing apple farm uncovers a locked cellar...",
  "poster_path": "/example-poster.jpg",
  "backdrop_path": "/example-backdrop.jpg",
  "release_date": "2026-10-10",
  "runtime": 128,
  "genres": [
    { "id": 9648, "name": "Mystery" },
    { "id": 18, "name": "Drama" }
  ],
  "vote_average": 8.4
}
```

## Failure behavior

| Case | App behavior |
|---|---|
| `Network` (`IOException`) | The no-connection bottom sheet opens (once for the screen). After it is dismissed, the header shows the inline error card with Retry. |
| 5xx, 429 | Inline error state with Retry. The reviews section keeps its own state. |
| 404 | "This movie is not available." with a Back action. No Retry, since it cannot succeed. |
| Parse failure | Generic error state. No crash. |

The mapping from HTTP status to app error is in [tmdb_overview.md](tmdb_overview.md#errors).
