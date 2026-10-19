package app.moviedb.core.di

import app.moviedb.core.data.local.FavoriteDao
import app.moviedb.core.data.network.service.ApiService
import app.moviedb.core.data.repository.FavoriteRepository
import app.moviedb.core.data.repository.FavoriteRepositoryImpl
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.data.repository.MovieRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun bindMovieRepository(apiService: ApiService): MovieRepository {
        return MovieRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(dao: FavoriteDao): FavoriteRepository {
        return FavoriteRepositoryImpl(dao)
    }
}