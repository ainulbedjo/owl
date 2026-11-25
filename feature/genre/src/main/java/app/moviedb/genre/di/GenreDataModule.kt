package app.moviedb.genre.di

import app.moviedb.genre.data.network.GenreApi
import app.moviedb.genre.data.repository.GenreRepository
import app.moviedb.genre.data.repository.GenreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GenreDataModule {

    @Provides
    @Singleton
    fun provideGenreApi(retrofit: Retrofit): GenreApi = retrofit.create(GenreApi::class.java)

    @Provides
    @Singleton
    fun provideGenreRepository(api: GenreApi): GenreRepository = GenreRepositoryImpl(api)
}
