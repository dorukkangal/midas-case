package com.midas.features.home.ui.mapper

import com.midas.core.ui.mapper.toAmountUiModel
import com.midas.core.ui.mapper.toLargeNumberUiModel
import com.midas.core.ui.mapper.toPercentageUiModel
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.ui.model.CoinUiModel

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
