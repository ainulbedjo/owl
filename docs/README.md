# MovieDB Docs

Planning docs for MovieDB: a movie app built on the TMDB API, using MVVM, Hilt, and Material 3 Expressive.

A checklist of what the app does is in [requirements/requirements.md](requirements/requirements.md).

## How to read these docs

Each document owns one decision area. Read in this order:

1. **Requirements**: what the app must do.
2. **API**: what the backend gives us.
3. **Architecture and libraries**: how the app is built and why.
4. **Features**: behavior of each screen.
5. **Design**: how the screens look and feel.

Docs are the source of truth. If code and docs disagree, fix the doc in the same change.

## Index

| Area | Document | Status |
|---|---|---|
| Requirements | [requirements/requirements.md](requirements/requirements.md) | Done |
| Architecture | [architecture.md](architecture.md) | Done |
| Libraries | [libraries.md](libraries.md) | Done |
| Features | [features/home.md](features/home.md), [features/detail.md](features/detail.md), [features/favorites.md](features/favorites.md) | Done |
| Design | `design/design_system.md`, `design/components/*.md` | Planned |
| API | [api/tmdb_overview.md](api/tmdb_overview.md), [api/movie_lists.md](api/movie_lists.md), [api/movie_detail.md](api/movie_detail.md), [api/movie_reviews.md](api/movie_reviews.md) | Done |
| To do | [todo/data-fetch.md](todo/data-fetch.md) | Open |

Links turn into real links as each document is written.

## Decisions so far

- Detail screen fetches `GET /movie/{id}`, so navigation needs only the movie id.
- Home carousels load page 1 only, with no pagination.
- Multi-module project with a shared `:core` module.
- Stack: Retrofit, kotlinx.serialization, Coil, Room, Hilt.
- Errors: only no-connection (`IOException`) opens a bottom sheet. Server and other errors stay as inline cards with Retry. See [api/tmdb_overview.md](api/tmdb_overview.md#where-errors-are-shown).
- UI follows Material 3 Expressive.

## Layout

```
docs/
  README.md
  requirements/
    requirements.md
  architecture.md
  libraries.md
  features/
  design/
    components/
  api/
```

## Resources
- [Material 3 Expressive](https://m3.material.io/blog/building-with-m3-expressive) | M3 Expressive design system
- [Moviebase.app](https://moviebase.app/) | Direct inspiration for research purposes
- [ip-as-logo-skill](https://github.com/s1dashu/ip-as-logo-skill) | Skill to create the logo used.
- [Icon kitchen](https://icon.kitchen/) | To create a ```icon/ic_launcher``` resources used in the codebase.
- [themoviedb.org](https://www.themoviedb.org/) | API and data seeding.