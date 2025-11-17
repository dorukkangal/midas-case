package com.midas.features.home.data.remote.mapper

import com.midas.features.home.data.remote.model.CoinResponse
import com.midas.features.home.domain.model.Coin

fun CoinResponse.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    image = image,
    currentPrice = currentPrice,
    marketCap = marketCap,
    marketCapRank = marketCapRank,
    priceChangePercentage24h = priceChangePercentage24h,
)
