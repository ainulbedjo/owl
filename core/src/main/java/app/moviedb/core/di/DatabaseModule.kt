package app.moviedb.core.di

import android.content.Context
import androidx.room.Room
import app.moviedb.core.data.local.FavoriteDao
import app.moviedb.core.data.local.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MovieDatabase =
        Room.databaseBuilder(context, MovieDatabase::class.java, "moviedb.db").build()

    @Provides
    fun provideFavoriteDao(database: MovieDatabase): FavoriteDao = database.favoriteDao()
}
