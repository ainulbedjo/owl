# Genre list

One endpoint returns the official movie genres. The app uses it for the genre list screen, for the genre pills at the bottom of Home, and to turn the `genre_ids` on a list item into names. Common rules (base URL, auth, errors, images) are in [tmdb_overview.md](tmdb_overview.md).

| What | Endpoint | Reference |
|---|---|---|
| Official movie genres | `GET /3/genre/movie/list` | https://developer.themoviedb.org/reference/genre-movie-list |

## Request

`GET` with no body and no path parameters.

| Parameter | In | Type | Default | Notes |
|---|---|---|---|---|
| `language` | query | string | `en` | Not sent by the app. The reference gives no other notes. |

Authentication is the same bearer token as every other call (see [tmdb_overview.md](tmdb_overview.md#authentication)).

Retrofit sketch:

```kotlin
interface MovieApi {
    @GET("genre/movie/list") suspend fun genres(): GenreListDto
}
```

## Response (200)

| Field | Type | Notes |
|---|---|---|
| `genres` | array of genre items | The reference lists no `required` fields. |
| `genres[].id` | integer | Example `28`. Used as the filter value on Discover. |
| `genres[].name` | string | Example `"Action"`. Display name. |

The reference documents only the 200 response. It lists no 401 or 404 body, and nothing about rate limits or caching.

Sample response (the same 19 ids as the reference example, with English names):

```json
{
  "genres": [
    { "id": 28, "name": "Action" },
    { "id": 12, "name": "Adventure" },
    { "id": 16, "name": "Animation" },
    { "id": 35, "name": "Comedy" },
    { "id": 80, "name": "Crime" },
    { "id": 99, "name": "Documentary" },
    { "id": 18, "name": "Drama" },
    { "id": 10751, "name": "Family" },
    { "id": 14, "name": "Fantasy" },
    { "id": 36, "name": "History" },
    { "id": 27, "name": "Horror" },
    { "id": 10402, "name": "Music" },
    { "id": 9648, "name": "Mystery" },
    { "id": 10749, "name": "Romance" },
    { "id": 878, "name": "Science Fiction" },
    { "id": 10770, "name": "TV Movie" },
    { "id": 53, "name": "Thriller" },
    { "id": 10752, "name": "War" },
    { "id": 37, "name": "Western" }
  ]
}
```

**Not in the reference:** the example body on the reference page has German names (`Abenteuer`, `Komödie`, `Krimi`), although the documented default for `language` is `en`. The page does not explain this. The ids are what the app depends on. Check the names with a real call when the network layer is built.

## Mapping to the domain model

The DTO maps to a small `Genre` model, proposed here and final in [architecture.md](../architecture.md).

| `Genre` field | Type | From | Rule |
|---|---|---|---|
| `id` | `Int` | `id` | Copy. |
| `name` | `String` | `name` | Copy. Trim whitespace. |

Map each item on its own. An item that fails to parse is dropped and the others still show. The mapper is a pure function with unit tests for: normal item, blank name, empty `genres` array.

## Where the data is used

| Place | Needs | Notes |
|---|---|---|
| Genres screen | `id`, `name` | Grid of all genres. Tapping one opens Discover with that id. |
| Home, bottom section | `id`, `name` | Genre pills. Tapping one opens Discover with that id. |
| Movie rows | `name` from `genre_ids` | List items carry only ids (see [movie_lists.md](movie_lists.md#movie-item)). Look the name up in this list. An unknown id is skipped. |
| Discover | `id` | **Not in the reference:** by known TMDB behavior the id goes to `GET /3/discover/movie` as `with_genres`. See [discover_movie.md](discover_movie.md). |

## Behavior to decide when this is built

These are proposals, not facts from the reference.

- The genre list is small and rarely changes. Load it once per process and share it between the Genres screen, the Home pills, and the movie rows. A failed load is retried on the next screen that needs it.
- Genres screen: loading shows skeleton tiles, failure shows the inline error card with **Retry** (see [Where errors are shown](tmdb_overview.md#where-errors-are-shown)).
- Home pills: Home is already made of independent sections. If the genre call fails, this section shows the same inline error card with **Retry**, and the other sections are not affected.
- Movie rows: if the genre list is not loaded, the genre chip is hidden. The row stays usable.
