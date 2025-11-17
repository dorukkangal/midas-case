package com.midas.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.midas.features.favorites.data.local.dao.FavoriteCoinDao
import com.midas.features.favorites.data.local.model.FavoriteCoinEntity

@Database(
    entities = [
        FavoriteCoinEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteCoinDao(): FavoriteCoinDao
}
