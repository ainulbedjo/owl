# Discover movies

One endpoint finds movies with filters and sort options. The app uses it for two screens: movies of one genre, and an endless list of all movies. Both load page after page. Common rules (base URL, auth, errors, images) are in [tmdb_overview.md](tmdb_overview.md). The genre ids come from [genre_list.md](genre_list.md).

| Screen | Endpoint | Reference |
|---|---|---|
| Discover by genre, Explore | `GET /3/discover/movie` | https://developer.themoviedb.org/reference/discover-movie |

The reference summary is "Find movies using over 30 filters and sort options." It documents only the 200 response. It says nothing about page limits, rate limits, or caching.

## Request

`GET` with no body. None of the parameters is required.

### Parameters the app sends

| Parameter | Type | Default | Used for |
|---|---|---|---|
| `page` | integer (int32) | `1` | Every request. Starts at 1 and goes up by one for each page. |
| `with_genres` | string | none | Discover by genre. One genre id, for example `9648`. Not sent by Explore. |
| `sort_by` | string | `popularity.desc` | The sort buttons on Discover. Values are in the table below. |
| `vote_count.gte` | number (float) | none | Added when sorting by rating, so a movie with two votes cannot rank first. Value `200`, the same floor TMDB uses for Top Rated (see [movie_lists.md](movie_lists.md#what-each-endpoint-is)). |
| `primary_release_date.lte` | string (date) | none | Added when sorting by newest, set to today, so unreleased movies do not fill the top of the list. |

Sort buttons on the Discover screen:

| Button | `sort_by` | Extra parameters |
|---|---|---|
| Popular | `popularity.desc` (the default, can be left out) | none |
| Top rated | `vote_average.desc` | `vote_count.gte=200` |
| Newest | `primary_release_date.desc` | `primary_release_date.lte=<today>` |

The extra parameters in the last two rows are **our decision**. The reference does not say how these filters affect the ranking.

Explore sends only `page`. It takes the default sort.

### Parameters the app does not send

| Parameter | Type | Default | Notes |
|---|---|---|---|
| `language` | string | `en-US` | Not sent. See the decision in [tmdb_overview.md](tmdb_overview.md#common-query-parameters). |
| `region` | string | none | Not sent. |
| `include_adult` | boolean | `false` | Not sent. The default is what we want. |
| `include_video` | boolean | `false` | Not sent. |
| `without_genres` | string | none | Not sent. The reference gives no separator rule for it. |

The reference lists about 40 more filters (cast, crew, companies, keywords, runtime, release date ranges, watch providers, certification, and others). The app uses none of them. They are in the reference when a later feature needs one.

### Multiple values

The reference says a comma means AND and a pipe means OR for filters such as `with_genres`. The app sends one genre id, so it does not use either.

### All `sort_by` values

`original_title.asc`, `original_title.desc`, `popularity.asc`, `popularity.desc`, `revenue.asc`, `revenue.desc`, `primary_release_date.asc`, `primary_release_date.desc`, `title.asc`, `title.desc`, `vote_average.asc`, `vote_average.desc`, `vote_count.asc`, `vote_count.desc`.

Retrofit sketch:

```kotlin
interface MovieApi {
    @GET("discover/movie")
    suspend fun discover(
        @Query("page") page: Int,
        @Query("with_genres") genreId: Int? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("vote_count.gte") minVoteCount: Int? = null,
        @Query("primary_release_date.lte") releasedBefore: String? = null,
    ): MovieListDto
}
```

Retrofit leaves out a `null` query value, so one function serves both screens. The response has the same shape as the three Home lists, so `MovieListDto` is reused.

## Response (200)

The envelope is the same one every list uses (see [movie_lists.md](movie_lists.md#response-200)):

| Field | Type | Notes |
|---|---|---|
| `page` | integer | The page that was asked for. |
| `results` | array of movie items | 20 items per page in the examples. |
| `total_pages` | integer | The reference example shows `38020`. See [Page limit](#page-limit). |
| `total_results` | integer | The reference example shows `760385`. Shown on screen as the count (for example "20 of 4,812 movies"). |

The movie item has the same fields as in [movie_lists.md](movie_lists.md#movie-item): `id`, `title`, `overview`, `poster_path`, `backdrop_path`, `release_date`, `vote_average`, `vote_count`, `popularity`, `genre_ids`, `adult`, `video`, and the two unused originals. The reference gives `adult` and `video` a schema default of `true` while its example shows `false`, as on the Home lists. We read neither field.

One difference from the Home lists: **`genre_ids` is now used.** Explore shows the first genre of each row as a chip. The name comes from [genre_list.md](genre_list.md).

## Page limit

The reference does not state a limit, and its own example reports `total_pages` of 38020. **Not in the reference:** TMDB error code 22 says pages go from 1 to 500 (see [tmdb_overview.md](tmdb_overview.md#pagination)). A request for a page above 500 fails with HTTP 400.

**Our decision:** the app treats the last page as `min(total_pages, 500)`. At that page the list ends, and the screen shows the end card: "TMDB serves up to 500 pages, so that is everything this list can show." The counter on Explore therefore tops out at 10,000 movies (500 pages of 20).

## Mapping to the domain model

The item maps to the shared `Movie` model from [movie_lists.md](movie_lists.md#mapping-to-the-domain-model), with one added field.

| `Movie` field | Type | From | Rule |
|---|---|---|---|
| `genreIds` | `List<Int>` | `genre_ids` | Copy. A missing array becomes an empty list. |

A page maps to a small paging model, proposed here and final in [architecture.md](../architecture.md):

| `MoviePage` field | Type | From | Rule |
|---|---|---|---|
| `items` | `List<Movie>` | `results` | Map each item on its own. An item that fails to parse is dropped, the rest stay. |
| `page` | `Int` | `page` | Copy. |
| `totalPages` | `Int` | `total_pages` | Copy. |
| `totalResults` | `Int` | `total_results` | Copy. |
| `hasMore` | `Boolean` | derived | `page < min(totalPages, 500)`. |

The mapper is a pure function. Unit tests: normal page, empty `results`, `null` poster, missing `genre_ids`, `page` equal to the last page gives `hasMore = false`.

## Paging behavior

These rules describe the endless scroll on Discover and Explore. They are proposals for the data layer and the ViewModels.

- **One request at a time.** While a page is loading, a second request for the next page is not started. A request for a page that failed is started only by **Retry**.
- **Trigger.** The next page is requested when the viewer is within about 700 dp of the end of the loaded list, so the new items usually arrive before the viewer reaches the bottom.
- **Append.** A new page is added after the items already shown. Scroll position and loaded items stay as they are.
- **Duplicates.** **Not in the reference:** ranking can change between two requests, so the same movie can come back on a later page. The ViewModel drops an item whose `id` is already in the list. The list uses the movie id as its stable key.
- **Changing the sort or the genre** throws away the loaded pages and starts again at page 1, with the first-load skeleton.
- **Rotation** keeps the loaded pages and the scroll position, because they live in the ViewModel. **Process death** reloads from page 1, because there is no response cache in v1.
- **End.** When `hasMore` is `false` the footer shows the end card with **Back to top**.

## Where the data is used

| Screen | Request | Card | Needs |
|---|---|---|---|
| Discover by genre | `with_genres`, `sort_by`, `page` | Poster card with a rating badge, 2 columns (`w342`) | `posterPath`, `title`, `releaseDate`, `rating`, `totalResults` |
| Explore | `page` | Row with poster, title, date, rating chip, genre chip, overview (`w185`) | `posterPath`, `title`, `releaseDate`, `rating`, `overview`, `genreIds`, `totalResults` |

Image sizes follow [tmdb_overview.md](tmdb_overview.md#images).

## Failure behavior

Errors map to `AppError` as in [tmdb_overview.md](tmdb_overview.md#errors). Where they show depends on which page failed:

| Case | Behavior |
|---|---|
| First page, `Network` | The no-connection sheet, then the inline error card with **Retry**. |
| First page, any other error | The inline error card with **Retry**. |
| First page, empty `results` | The empty state, not an error. |
| A later page, any error including `Network` | An inline card at the end of the list: "Could not load more movies", with **Retry**. The loaded items stay. **Our decision:** no sheet here, because it would cover a list the viewer is reading. This is an exception to [Where errors are shown](tmdb_overview.md#where-errors-are-shown) and that page should say so when this is built. |
| A later page, `RateLimited` | The same inline card. **Retry** is a manual tap, and the app does not retry on its own. |
