# Requirements

What the app must do and the quality bar it is built to. This file is a checklist: the feature docs in [features/](../features/) hold the detailed behavior.

## Brief

Build a movie Android application on the TMDB API with three screens: Home, Detail, and Favorites.

## Rules

- [ ] Use the TMDB API (reference: https://developer.themoviedb.org/reference).
- [ ] Use an MVVM architecture.
- [ ] Keep API credentials out of the repository (see the setup note in the project README).

## Features

### 1. Home

- [ ] Popular movies list (`GET /movie/popular`).
- [ ] Top rated movies list (`GET /movie/top_rated`).
- [ ] Now playing movies list (`GET /movie/now_playing`).
- [ ] Favorite icon button that navigates to the Favorites screen.

### 2. Detail

- [ ] Show movie reviews (`GET /movie/{id}/reviews`).
- [ ] Favorite: save the movie to local storage and remove it from local storage.
- [ ] Share: show a bottom sheet.

### 3. Favorites

- [ ] Show the list of the user's favorite movies.

## Goals

- [ ] No bugs and no crashes.
- [ ] All features are implemented and run well.
- [ ] Every library choice is justified, in [libraries.md](../libraries.md).

## Also in scope

- [ ] Unit tests.
- [ ] Dependency injection with Hilt.
- [ ] Android Jetpack libraries.

## Screen overview

| Screen | Layout |
|---|---|
| Home | App bar with title and a favorite icon. Three sections: Popular (wide cards in a horizontal strip), Top Rated and Now Playing (smaller poster cards, each with title and release date). |
| Detail | Large backdrop image at the top, then title, release date, description, and review text. Favorite and share actions at the bottom. |
| Favorites | App bar titled "Favorite Movie". Vertical list: poster on the left, title, release date, and truncated description on the right. |

## Notes and open points

- The list endpoints do not carry a full description, so Detail also calls `GET /movie/{id}`.
- Error, empty, and loading states are defined in the feature docs, since "no bugs and no crashes" depends on them.
- The visual style follows Material 3 Expressive (see `design/design_system.md`, planned).
