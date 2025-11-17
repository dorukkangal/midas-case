package com.midas.features.favorites.ui.mapper

import com.midas.core.ui.mapper.toAmountUiModel
import com.midas.core.ui.mapper.toLargeNumberUiModel
import com.midas.core.ui.mapper.toPercentageUiModel
import com.midas.features.favorites.ui.model.CoinUiModel
import com.midas.features.home.domain.model.Coin

fun Coin.toCoinUiModel() = CoinUiModel(
    id = id,
    name = name,
    symbol = symbol,
    image = image,
    currentPrice = currentPrice?.toAmountUiModel(),
    marketCap = marketCap?.toDouble()?.toLargeNumberUiModel(),
    marketCapRank = marketCapRank,
    priceChangePercentage24h = priceChangePercentage24h?.toPercentageUiModel(),
)

fun CoinUiModel.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    image = image,
    currentPrice = currentPrice?.amount,
    marketCap = marketCap?.number?.toLong(),
    marketCapRank = marketCapRank,
    priceChangePercentage24h = priceChangePercentage24h?.percentage,
)
