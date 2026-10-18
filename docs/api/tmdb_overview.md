# TMDB API overview

The app reads from The Movie Database (TMDB) API v3. This page covers what every endpoint has in common: base URL, authentication, images, pagination, errors, and rate limits. Endpoint-specific contracts are in the other `api/` pages:

- [movie_lists.md](movie_lists.md): Popular, Top Rated, Now Playing.
- [movie_detail.md](movie_detail.md): movie details.
- [movie_reviews.md](movie_reviews.md): reviews.

Source: the TMDB reference at https://developer.themoviedb.org/reference. Facts marked **Not in the reference** come from known TMDB behavior or are our own decisions. Verify them with a real call when the network layer is built.

## Endpoints used

| Screen | Endpoint | Doc |
|---|---|---|
| Home | `GET /3/movie/popular` | [movie_lists.md](movie_lists.md) |
| Home | `GET /3/movie/top_rated` | [movie_lists.md](movie_lists.md) |
| Home | `GET /3/movie/now_playing` | [movie_lists.md](movie_lists.md) |
| Detail | `GET /3/movie/{movie_id}` | [movie_detail.md](movie_detail.md) |
| Detail | `GET /3/movie/{movie_id}/reviews` | [movie_reviews.md](movie_reviews.md) |

Favorites use no endpoint. They are read from the local database.

## Base URL

```
https://api.themoviedb.org/3/
```

Retrofit needs the trailing slash on the base URL. Endpoint paths are then written without a leading slash (`movie/popular`).

## Authentication

Every call sends the **API Read Access Token** as a bearer token:

```
Authorization: Bearer <read access token>
accept: application/json
```

The reference describes the scheme as an API key in the `Authorization` header, bearer format. There is no `api_key` query parameter in our calls.

How the token reaches the app:

1. Put it in `local.properties`, which is not committed.
2. Gradle reads it and exposes it as a `BuildConfig` field in `:core`.
3. An OkHttp interceptor adds the header to each request.

Rules:

- The token must never appear in git, in these docs, or in logs. Disable HTTP body and header logging in release builds.
- **Not in the reference:** a token inside an APK can be extracted. That is acceptable for this test. A production app would proxy TMDB through its own backend.

## Common query parameters

| Parameter | Type | Default | Used by | Our value |
|---|---|---|---|---|
| `language` | string | `en-US` | all five endpoints | Not sent. We rely on the default `en-US`. |
| `page` | int32 | `1` | lists, reviews | Not sent. Page 1 only. |
| `region` | string (ISO-3166-1) | none | lists | Not sent. |

**Decision:** language stays at `en-US`. Passing the device locale can return empty `overview` text for less-translated movies, which makes cards look broken. Revisit if localization becomes a goal.

## Pagination

List and review responses share this envelope:

| Field | Type | Meaning |
|---|---|---|
| `page` | integer | Current page. |
| `results` | array | Items for this page. |
| `total_pages` | integer | Number of pages. |
| `total_results` | integer | Number of items across all pages. |

The app loads page 1 only, with no paging (see [README decisions](../README.md#decisions-so-far)). The DTOs still carry `page`, `total_pages`, and `total_results` so that paging can be added later without changing the contract.

**Not in the reference:** error code 22 in the TMDB error list says pages start at 1 and go up to 500.

## Images

API responses carry image *paths* (`poster_path`, `backdrop_path`), never full URLs. Build the URL as:

```
{base_url}{size}{path}
https://image.tmdb.org/t/p/w500/1E5baAaEse26fej7uHcjOgEE2t2.jpg
```

- The image guide gives the URL pattern and says to fetch the current `base_url` and size list from `GET /3/configuration`.
- **Not in the reference page we read:** the concrete size lists. From the configuration endpoint, posters offer `w92, w154, w185, w342, w500, w780, original` and backdrops offer `w300, w780, w1280, original`. Confirm against `/configuration` before relying on a size.
- We hard-code `https://image.tmdb.org/t/p/` and a size per use. This avoids a startup request that can fail.

Sizes per use (**our decision**, based on the layouts in the prototype at 2x to 3x density):

| Where | Size | Why |
|---|---|---|
| Home, Popular card | poster `w500` | The card is about 296dp wide. |
| Home, Top Rated and Now Playing poster | poster `w342` | The poster is 132dp wide. |
| Detail header | backdrop `w780` | Full-width image. |
| Favorites row poster | poster `w185` | The poster is 92dp wide. |
| Share sheet thumbnail | poster `w154` | The thumbnail is 56dp wide. |

Both `poster_path` and `backdrop_path` can be missing. See the nullability rule below.

## Nullability and defensive parsing

The reference lists every field as a plain type and marks none as nullable. **Not in the reference:** real TMDB responses return `null` or empty strings for several fields. The DTOs must tolerate this, or one bad movie crashes a whole list.

| Field | Real-world behavior | DTO type |
|---|---|---|
| `poster_path`, `backdrop_path` | `null` for movies without art | `String?` |
| `release_date` | `""` or missing for unreleased titles | `String?` |
| `overview`, `tagline` | `""` is common | `String?` with a default of `null` or `""` |
| `runtime` | `0` or `null` | `Int?` |
| `author_details.rating` | number or `null` (the reference gives no type) | `Double?` |

Also set `ignoreUnknownKeys = true` on the JSON parser, because the API returns many fields we ignore and may add more.

The UI shows a placeholder for a missing image and hides a missing date, tagline, or runtime instead of showing "null".

## Errors

The reference documents **only the 200 response** for all five endpoints. It lists no 401, 404, or 429 bodies. The TMDB error guide lists numbered status codes with an HTTP code and a message. The ones that matter to us:

| TMDB code | HTTP | Meaning | When it happens in this app |
|---|---|---|---|
| 7 | 401 | Invalid API key | Missing or wrong token in `local.properties`. |
| 3 | 401 | Authentication failed | Token without permission. |
| 34 | 404 | Requested resource not found | Detail or reviews for an unknown `movie_id`. |
| 6 | 404 | Invalid id | Same, for a bad id. |
| 22 | 400 | Invalid page | Not expected: we only request page 1. |
| 25 | 429 | Request count over the allowed limit | Too many requests in a short time. |
| 24 | 504 | Request to backend timed out | TMDB slow or unavailable. |
| 43 | 502 | Could not connect to the backend server | TMDB slow or unavailable. |
| 9, 46 | 503 | Service offline or under maintenance | TMDB down. |
| 11, 15 | 500 | Internal error or failed | TMDB failure. |

The error guide does not show the response body shape. **Not in the reference:** TMDB commonly returns a body like this, but verify it on a real failing call:

```json
{ "success": false, "status_code": 7, "status_message": "Invalid API key: You must be granted a valid key." }
```

The app does not parse this body for control flow. It maps the HTTP status and exceptions to a small set of app errors (the full model lives in `architecture.md`, planned):

| Cause | App error | UI behavior |
|---|---|---|
| `IOException` (no connection, timeout) | `Network` | **No-connection bottom sheet**, then the inline card with Retry. See [Where errors are shown](#where-errors-are-shown). |
| HTTP 401 or 403 | `Unauthorized` | Inline error card. Log it. This is a setup problem, not a user problem. |
| HTTP 404 | `NotFound` | Detail only: "This movie is not available." |
| HTTP 429 | `RateLimited` | Inline error card with Retry. Do not auto-retry in a tight loop. |
| HTTP 5xx | `Server` | Inline error card with Retry. |
| Parse failure | `Unexpected` | Inline error card. Never crash. |

Each Home section fails on its own: one failed list does not hide the other two.

### Where errors are shown

**Rule:** only `Network` (any `IOException`) opens the no-connection bottom sheet. Every other error stays inline in the section that failed.

| | No-connection sheet | Inline error card |
|---|---|---|
| Triggered by | `Network` | `Unauthorized`, `NotFound`, `RateLimited`, `Server`, `Unexpected` |
| Scope | The whole screen | The one section that failed |
| Content | Owl illustration, "No connection", a hint, **Try again**, **Open favorites** | Message and **Retry** |
| Dismiss | Scrim tap, back, or either button | None. It stays until a retry succeeds. |

Behavior:

1. **One sheet per screen, not per call.** If the three Home calls fail together, the app shows one sheet. A second failure while the sheet is open does not stack another.
2. **The inline card stays behind the sheet.** After the sheet is dismissed, each failed section shows its inline card with Retry, so the user is never left on a blank screen.
3. **Try again** re-runs every failed call on the current screen. If the screen loads, the sheet closes. If the device is still offline, the sheet stays open.
4. **Open favorites** closes the sheet and navigates to Favorites. Favorites read the local database and work offline.
5. The sheet is modal and uses the same bottom sheet component as the share sheet (see `design/components/share_bottom_sheet.md`, planned).
6. A timeout is also an `IOException`, so it opens the sheet and the copy says "No connection". A `504` from TMDB is an HTTP error, so it is `Server` and stays inline.

The illustration is `no_connection_clear` (transparent background) in `app/src/main/res/drawable/`. It is shown inside a rounded shape container so its cropped edges look intentional. See the "No connection sheet" board in the UI canvas.

## Rate limits

The rate-limit guide gives no exact figure. It says the upper limit is "somewhere in the 40 requests per second range", that it can change at any time, and that clients should respect a `429` response. It documents no rate-limit headers.

Impact on this app: Home makes three calls on open and Detail makes two. That is far below the limit. No client-side throttling is needed. A manual Retry must not fire more than one request per tap.

## Caching

**Our decision:** use OkHttp's default behavior. Keep no response cache in the database in v1. The only locally stored data is the favorites snapshot (see `features/favorites.md`, planned).
