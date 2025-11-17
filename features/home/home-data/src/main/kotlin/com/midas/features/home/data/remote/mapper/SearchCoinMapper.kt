package com.midas.features.home.data.remote.mapper

import com.midas.features.home.data.remote.model.SearchCoin
import com.midas.features.home.domain.model.Coin

fun SearchCoin.toCoin() = Coin(
    id = id,
    symbol = symbol,
    name = name,
    image = image,
    currentPrice = 0.0, // Search API doesn't provide USD price directly
    marketCap = null,
    marketCapRank = marketCapRank,
    priceChangePercentage24h = null,
)
