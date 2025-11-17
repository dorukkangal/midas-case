package com.midas.features.favorites.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_coins",
    indices = [Index(value = ["coin_id"], unique = true)]
)
data class FavoriteCoinEntity(
    @PrimaryKey
    @ColumnInfo(name = "coin_id")
    val coinId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)
