# MovieDB Docs

Planning docs for the Parkee Android Engineer Test: a movie app built on the TMDB API, using MVVM, Hilt, and Material 3 Expressive.

The source brief is [requirements/requirements.pdf](requirements/requirements.pdf). A checklist version is in [requirements/requirements.md](requirements/requirements.md).

## How to read these docs

Each document owns one decision area. Read in this order:

1. **Requirements**: what the test asks for.
2. **API**: what the backend gives us.
3. **Architecture and libraries**: how the app is built and why.
4. **Features**: behavior of each screen.
5. **Design**: how the screens look and feel.

Docs are the source of truth. If code and docs disagree, fix the doc in the same change.

## Index

| Area | Document | Status |
|---|---|---|
| Requirements | [requirements/requirements.md](requirements/requirements.md) | Done |
| Architecture | `architecture.md` | Planned |
| Libraries | `libraries.md` | Planned |
| Features | `features/home.md`, `features/detail.md`, `features/favorites.md` | Planned |
| Design | `design/design_system.md`, `design/components/*.md` | Planned |
| API | `api/tmdb_overview.md`, `api/movie_lists.md`, `api/movie_detail.md`, `api/movie_reviews.md` | Planned |

Links turn into real links as each document is written.

## Decisions so far

- Detail screen fetches `GET /movie/{id}`, so navigation needs only the movie id.
- Home carousels load page 1 only, with no pagination.
- Multi-module project with a shared `:core` module.
- Stack: Retrofit, kotlinx.serialization, Coil, Room, Hilt.
- UI follows Material 3 Expressive. The PDF wireframe is a layout guide, not a visual spec.

## Layout

```
docs/
  README.md
  requirements/
    requirements.pdf
    requirements.md
  architecture.md
  libraries.md
  features/
  design/
    components/
  api/
```
