package app.moviedb.core.di

import app.moviedb.core.data.network.service.ApiService
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.data.repository.MovieRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Binds
    @Singleton
    fun bindMovieRepository(apiService: ApiService): MovieRepository {
        return MovieRepositoryImpl(apiService)
    }
}