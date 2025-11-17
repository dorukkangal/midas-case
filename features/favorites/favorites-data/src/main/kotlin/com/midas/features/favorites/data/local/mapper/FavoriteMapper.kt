package com.midas.features.favorites.data.local.mapper

import com.midas.features.favorites.data.local.model.FavoriteCoinEntity
import com.midas.features.home.domain.model.Coin

fun Coin.toFavoriteCoinEntity(): FavoriteCoinEntity = FavoriteCoinEntity(
    coinId = id,
    name = name,
    symbol = symbol,
    image = image,
    addedAt = System.currentTimeMillis()
)

fun FavoriteCoinEntity.toCoin(): Coin = Coin(
    id = coinId,
    symbol = symbol,
    name = name,
    image = image,
    currentPrice = null,
    marketCap = null,
    marketCapRank = null,
    priceChangePercentage24h = null,
)
