package com.midas.core.database.di

import android.content.Context
import androidx.room.Room
import com.midas.core.database.AppDatabase
import com.midas.features.favorites.data.local.dao.FavoriteCoinDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            klass = AppDatabase::class.java,
            name = "app_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteCoinDao(database: AppDatabase): FavoriteCoinDao {
        return database.favoriteCoinDao()
    }
}
