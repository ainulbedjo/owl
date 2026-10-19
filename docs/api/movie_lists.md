# Movie lists

Three endpoints feed the Home screen. They share the same query parameters and almost the same response. Common rules (base URL, auth, errors, images) are in [tmdb_overview.md](tmdb_overview.md).

| Section | Endpoint | Reference |
|---|---|---|
| Popular | `GET /3/movie/popular` | https://developer.themoviedb.org/reference/movie-popular-list |
| Top Rated | `GET /3/movie/top_rated` | https://developer.themoviedb.org/reference/movie-top-rated-list |
| Now Playing | `GET /3/movie/now_playing` | https://developer.themoviedb.org/reference/movie-now-playing-list |

## Request

All three are `GET` with no body.

| Parameter | In | Type | Default | Notes |
|---|---|---|---|---|
| `language` | query | string | `en-US` | Not sent by the app. |
| `page` | query | integer (int32) | `1` | Not sent by the app. Page 1 only. |
| `region` | query | string | none | ISO-3166-1 country code. Not sent by the app. |

Retrofit sketch:

```kotlin
interface MovieApi {
    @GET("movie/popular")     suspend fun popular(): MovieListDto
    @GET("movie/top_rated")   suspend fun topRated(): MovieListDto
    @GET("movie/now_playing") suspend fun nowPlaying(): NowPlayingDto
}
```

## Response (200)

Envelope, shared by all three:

| Field | Type | Notes |
|---|---|---|
| `page` | integer | Example: `1`. |
| `results` | array of movie items | 20 items per page in the reference example. |
| `total_pages` | integer | |
| `total_results` | integer | |

`now_playing` adds one object:

| Field | Type | Notes |
|---|---|---|
| `dates` | object | Date range the list covers. |
| `dates.minimum` | string | Date, example `2023-05-03`. |
| `dates.maximum` | string | Date, example `2023-05-03`. |

The app ignores `dates`. Keep it in `NowPlayingDto` only if we want to show the range later. With `ignoreUnknownKeys = true`, `NowPlayingDto` can be the same class as `MovieListDto`.

### Movie item

The reference lists these fields for each item in `results`:

| Field | Reference type | DTO type | Notes |
|---|---|---|---|
| `id` | integer | `Int` | Movie id. Used for navigation and as the favorites key. |
| `title` | string | `String` | |
| `original_title` | string | `String` | Not used. |
| `original_language` | string | `String` | Not used. |
| `overview` | string | `String?` | Can be empty. |
| `poster_path` | string | `String?` | Can be `null`. See [tmdb_overview.md](tmdb_overview.md#nullability-and-defensive-parsing). |
| `backdrop_path` | string | `String?` | Can be `null`. Used by the wide Now Playing card (`w780`); falls back to the poster. |
| `release_date` | string | `String?` | Format `yyyy-MM-dd`. Can be empty. |
| `vote_average` | number | `Double` | Example `8.7`. |
| `vote_count` | integer | `Int` | Not used. |
| `popularity` | number | `Double` | Not used. |
| `genre_ids` | array of integer | `List<Int>` | Not used. Names come from the detail call. |
| `adult` | boolean | `Boolean` | Not used. |
| `video` | boolean | `Boolean` | Not used. |

The reference shows a schema default of `true` for `adult` and `video`, while its examples show `false`. The page does not explain this. We do not read either field, so it does not affect us.

## What each endpoint is

From the reference, each of these is a wrapper over the Discover call with preset filters:

| Endpoint | Equivalent Discover filters |
|---|---|
| `popular` | `include_adult=false`, `include_video=false`, `sort_by=popularity.desc` |
| `top_rated` | `include_adult=false`, `include_video=false`, `sort_by=vote_average.desc`, `without_genres=99,10755`, `vote_count.gte=200` |
| `now_playing` | `include_adult=false`, `include_video=false`, `sort_by=popularity.desc`, `with_release_type=2\|3` |

We call the dedicated endpoints, not Discover.

## Mapping to the domain model

The list DTO maps to one shared `Movie` model (proposed here, final in `architecture.md`, planned). Only the fields the UI shows survive.

| `Movie` field | Type | From | Rule |
|---|---|---|---|
| `id` | `Int` | `id` | Copy. |
| `title` | `String` | `title` | Copy. |
| `overview` | `String` | `overview` | `null` becomes `""`. |
| `posterPath` | `String?` | `poster_path` | Copy, `null` stays `null`. The UI builds the full URL with `TmdbImage.url(path, size)` for the size it needs. |
| `backdropPath` | `String?` | `backdrop_path` | Copy, `null` stays `null`. The wide Now Playing card uses it at `w780`, and falls back to `posterPath`. |
| `releaseDate` | `LocalDate?` | `release_date` | Parse `yyyy-MM-dd`. Empty or invalid becomes `null`. |
| `rating` | `Double` | `vote_average` | Copy. Display with one decimal. A value of `0.0` means "not rated", and the UI hides the rating chip or badge. |

Map each item on its own and drop nothing silently: if one item fails to parse, the others still show.

The mapper is a pure function. It gets unit tests for: normal item, `null` poster, empty `release_date`, empty `overview`.

## Where the data is used

| Section | Card | Needs |
|---|---|---|
| Popular | Wide card with poster art, title, date, and a rating chip | `posterPath` (`w500`), `title`, `releaseDate`, `rating` |
| Top Rated | Poster with a rating badge, title below, and date | `posterPath` (`w342`), `title`, `releaseDate`, `rating` |
| Now Playing | Wide card: backdrop with the date on it, title and rating below | `backdropPath` (`w780`, else `posterPath`), `title`, `releaseDate`, `rating` |

## Sample response

Illustrative data with a made-up movie. Real responses carry 20 items.

```json
{
  "page": 1,
  "results": [
    {
      "adult": false,
      "backdrop_path": "/example-backdrop.jpg",
      "genre_ids": [9648, 18],
      "id": 900101,
      "original_language": "en",
      "original_title": "Midnight Orchard",
      "overview": "When a night shift at a failing apple farm uncovers a locked cellar...",
      "popularity": 412.337,
      "poster_path": "/example-poster.jpg",
      "release_date": "2026-10-10",
      "title": "Midnight Orchard",
      "video": false,
      "vote_average": 8.4,
      "vote_count": 1204
    }
  ],
  "total_pages": 500,
  "total_results": 10000
}
```

## Failure behavior

The three calls run in parallel and fail independently. A failed section shows an inline error card with Retry while the other two stay visible (see `features/home.md`, planned).

The one exception is `Network` (`IOException`): it opens the no-connection bottom sheet, once for the screen, and the inline cards remain behind it. See [tmdb_overview.md](tmdb_overview.md#where-errors-are-shown). Error mapping is in [tmdb_overview.md](tmdb_overview.md#errors).
