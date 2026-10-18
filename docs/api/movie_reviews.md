# Movie reviews

One endpoint feeds the Reviews section of the Detail screen. This is a required feature. Common rules are in [tmdb_overview.md](tmdb_overview.md).

| Endpoint | Reference |
|---|---|
| `GET /3/movie/{movie_id}/reviews` | https://developer.themoviedb.org/reference/movie-reviews |

The reference summarizes it as "Get the user reviews for a movie."

## Request

| Parameter | In | Type | Required | Default | Notes |
|---|---|---|---|---|---|
| `movie_id` | path | integer (int32) | yes | none | The same id used for the detail call. |
| `language` | query | string | no | `en-US` | Not sent by the app. |
| `page` | query | integer (int32) | no | `1` | Not sent by the app. Page 1 only. |

Retrofit sketch:

```kotlin
@GET("movie/{movie_id}/reviews")
suspend fun reviews(@Path("movie_id") id: Int): ReviewListDto
```

**Decision:** load page 1 only. The Detail screen shows those reviews and nothing more. If a movie has more pages, they are not reachable in v1. This matches the Home decision and keeps the scope small.

## Response (200)

Envelope:

| Field | Type | Notes |
|---|---|---|
| `id` | integer | The movie id (example `550`). |
| `page` | integer | Example `1`. |
| `results` | array of review | |
| `total_pages` | integer | |
| `total_results` | integer | Example `8`. |

### Review

| Field | Reference type | DTO type | Notes |
|---|---|---|---|
| `id` | string | `String` | A review id such as `5b1c13b9c3a36848f2026384`. Not an integer. Used as the list key. |
| `author` | string | `String` | Username shown as the display name when `author_details.name` is blank. |
| `author_details` | object | `AuthorDetailsDto` | See below. |
| `content` | string | `String` | Review body. Can be long. |
| `created_at` | string | `String` | ISO-8601 timestamp, example `2018-06-09T17:51:53.359Z`. |
| `updated_at` | string | `String` | Same format. Not used. |
| `url` | string | `String` | Link to the review on themoviedb.org. Not used. |

### `author_details`

| Field | Reference type | DTO type | Notes |
|---|---|---|---|
| `name` | string | `String?` | Often `""`. |
| `username` | string | `String?` | |
| `avatar_path` | string | `String?` | See the avatar rule below. |
| `rating` | no type declared | `Double?` | The reference examples show numbers like `9.0` and `null`. A review may have no rating. |

### DTO

```kotlin
@Serializable
data class ReviewListDto(
    val id: Int,
    val page: Int,
    val results: List<ReviewDto>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int,
)

@Serializable
data class ReviewDto(
    val id: String,
    val author: String,
    @SerialName("author_details") val authorDetails: AuthorDetailsDto? = null,
    val content: String,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class AuthorDetailsDto(
    val name: String? = null,
    val username: String? = null,
    @SerialName("avatar_path") val avatarPath: String? = null,
    val rating: Double? = null,
)
```

## Mapping to the domain model

| `Review` field | Type | From | Rule |
|---|---|---|---|
| `id` | `String` | `id` | Copy. |
| `authorName` | `String` | `author_details.name`, then `author` | Use `name` when not blank, else `author`. |
| `avatarUrl` | `String?` | `author_details.avatar_path` | See the avatar rule. |
| `rating` | `Double?` | `author_details.rating` | Copy. `null` shows "No rating". |
| `content` | `String` | `content` | Copy. Trim whitespace. |
| `createdAt` | `LocalDate?` | `created_at` | Parse the ISO timestamp, keep the date. Invalid becomes `null`. |

### Avatar rule

**Not in the reference:** `avatar_path` is one of three things in real data.

| Value | Meaning | Mapped to |
|---|---|---|
| `null` or blank | No avatar | `null` |
| `/abc123.jpg` | A TMDB image path | `https://image.tmdb.org/t/p/w154/abc123.jpg` |
| `/https://www.gravatar.com/avatar/...` | A full Gravatar URL with a stray leading slash | Strip the leading `/`, keep the URL as is |

The prototype avoids network avatars and shows the author's initial in a decorative shape. We use the initial as the fallback whenever `avatarUrl` is `null` or the image fails to load. A reviewer without an avatar must never leave an empty gap.

## Display rules

- **Long content:** show 3 lines, then a "Read more" action that expands in place and becomes "Show less". Content can contain Markdown-like text. v1 shows it as plain text.
- **Rating:** a small pill with a star and one decimal. When `rating` is `null`, show "No rating" without the star.
- **Date:** the review date, formatted for the device locale.
- **Order:** keep the API order.

## Sample response

Illustrative data with made-up reviews, content shortened.

```json
{
  "id": 900101,
  "page": 1,
  "results": [
    {
      "author": "Mara Quill",
      "author_details": {
        "name": "",
        "username": "Mara Quill",
        "avatar_path": null,
        "rating": 9.0
      },
      "content": "A patient, beautifully shot film that trusts the audience...",
      "created_at": "2026-10-12T17:51:53.359Z",
      "id": "5b1c13b9c3a36848f2026384",
      "updated_at": "2026-10-13T15:58:09.421Z",
      "url": "https://www.themoviedb.org/review/5b1c13b9c3a36848f2026384"
    },
    {
      "author": "devon_reyes",
      "author_details": {
        "name": "",
        "username": "devon_reyes",
        "avatar_path": "/https://www.gravatar.com/avatar/example",
        "rating": null
      },
      "content": "Strong cast and a score that carries more than the script does...",
      "created_at": "2026-10-08T09:12:01.000Z",
      "id": "5b1c13b9c3a36848f2026385",
      "updated_at": "2026-10-08T09:12:01.000Z",
      "url": "https://www.themoviedb.org/review/5b1c13b9c3a36848f2026385"
    }
  ],
  "total_pages": 1,
  "total_results": 2
}
```

## States and failure behavior

| Case | Reviews section shows |
|---|---|
| Loading | Skeleton cards. |
| `results` is empty | "No reviews yet" empty state. This is a normal outcome, not an error. |
| `Network` (`IOException`) | Shares the Detail screen's no-connection bottom sheet. If the detail call also failed, only one sheet opens. After it is dismissed, the section shows the inline error card with Retry. |
| 5xx, 429 | An inline error card with Retry. The rest of the Detail screen stays visible. |
| 404 | Treated as an empty list. |
| Parse failure | Error card with Retry. No crash. |

Error mapping is in [tmdb_overview.md](tmdb_overview.md#errors).

## Tests

- Mapper: `name` blank falls back to `author`; `rating` `null`; all three avatar forms; invalid `created_at`.
- Repository with a fake API: empty `results` produces an empty list, not an error.
