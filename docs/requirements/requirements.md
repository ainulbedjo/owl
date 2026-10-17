# Requirements

Source: [requirements.pdf](requirements.pdf), "Android Engineer Test" from Parkee. This file restates it as a checklist. If the two differ, the PDF wins.

## Brief

Build a Movie Android application that uses the TMDB API. The PDF includes a wireframe of three screens: Home, Detail, and Favorite List.

## Rules

- [ ] Submit the project within 3 days of receiving the test.
- [ ] Upload the project to GitHub.
- [ ] Implement the TMDB API (https://developers.themoviedb.org/3).
- [ ] Use an MVVM or MVP architecture. This project uses MVVM.
- [ ] When complete, send the GitHub link and the APK file to HR.

## Features

### 1. Home

- [ ] Popular movies list (`GET /movie/popular`).
- [ ] Top rated movies list (`GET /movie/top_rated`).
- [ ] Now playing movies list (`GET /movie/now_playing`).
- [ ] Favorite icon button that navigates to the Favorite List page.

### 2. Detail

- [ ] Show movie reviews (`GET /movie/{id}/reviews`).
- [ ] Favorite: save the movie to local storage and remove it from local storage.
- [ ] Share: show a bottom sheet.

### 3. Favorite List

- [ ] Show the list of the user's favorite movies.

## Goals

The submission is graded on these:

- [ ] No bugs and no crashes.
- [ ] All required features are implemented and run well.
- [ ] Library selection. Choices are part of the grading, so each is justified in `libraries.md`.

## Bonus points

- [ ] Unit tests.
- [ ] Dependency injection with Hilt.
- [ ] Android Jetpack.

## Wireframe summary

| Screen | Layout from the wireframe |
|---|---|
| Home | App bar with title and a favorite icon. Three sections: Popular (wide cards in a horizontal strip), Top Rated and Now Playing (smaller poster cards, each with title and release date). |
| Detail | Large backdrop image at the top, then title, release date, description, and review text. Favorite and share icons at the bottom. |
| Favorite List | App bar titled "Favorite Movie". Vertical list: poster on the left, title, release date, and truncated description on the right. |

## Notes and open points

- The PDF links to the older TMDB docs (`developers.themoviedb.org/3`). The current reference is at `developer.themoviedb.org/reference`. The endpoints are the same.
- The PDF lists no endpoint for the movie description on Detail. We add `GET /movie/{id}` for it.
- The PDF says nothing about error, empty, or loading states. They are defined in the feature docs, since "no bugs and no crashes" depends on them.
- The visual style differs from the wireframe on purpose: the app uses Material 3 Expressive (see `design/design_system.md`, planned).
