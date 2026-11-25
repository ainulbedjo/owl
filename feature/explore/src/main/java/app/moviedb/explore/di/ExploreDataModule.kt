package app.moviedb.explore.di

import app.moviedb.explore.data.network.ExploreApi
import app.moviedb.explore.data.repository.ExploreRepository
import app.moviedb.explore.data.repository.ExploreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExploreDataModule {

    @Provides
    @Singleton
    fun provideExploreApi(retrofit: Retrofit): ExploreApi = retrofit.create(ExploreApi::class.java)

    @Provides
    @Singleton
    fun provideExploreRepository(api: ExploreApi): ExploreRepository = ExploreRepositoryImpl(api)
}
