package com.midas.features.detail.ui.mapper

import com.midas.core.ui.mapper.toAmountUiModel
import com.midas.core.ui.mapper.toLargeNumberUiModel
import com.midas.core.ui.mapper.toPercentageUiModel
import com.midas.features.detail.domain.model.CoinDetail
import com.midas.features.detail.domain.model.MarketData
import com.midas.features.detail.ui.model.CoinDetailUiModel
import com.midas.features.detail.ui.model.MarketDataUiModel
import com.midas.features.home.domain.model.Coin

fun CoinDetail.toCoinDetailUiModel() = CoinDetailUiModel(
    id = id,
    name = name,
    symbol = symbol,
    description = description,
    image = image,
    marketCapRank = marketCapRank,
    marketData = marketData.toMarketDataUiModel(),
    categories = categories,
    genesisDate = genesisDate,
)

fun MarketData.toMarketDataUiModel() = MarketDataUiModel(
    currentPrice = currentPrice?.toAmountUiModel(),
    marketCap = marketCap?.toDouble()?.toLargeNumberUiModel(),
    totalVolume = totalVolume?.toDouble()?.toLargeNumberUiModel(),
    high24h = high24h?.toAmountUiModel(),
    low24h = low24h?.toAmountUiModel(),
    priceChangePercentage24h = priceChangePercentage24h?.toPercentageUiModel(),
    priceChangePercentage7d = priceChangePercentage7d?.toPercentageUiModel(),
    priceChangePercentage30d = priceChangePercentage30d?.toPercentageUiModel(),
    circulatingSupply = circulatingSupply?.toLargeNumberUiModel(),
    totalSupply = totalSupply?.toLargeNumberUiModel(),
    maxSupply = maxSupply?.toLargeNumberUiModel(),
)

fun CoinDetailUiModel.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    image = image,
    currentPrice = marketData.currentPrice?.amount,
    marketCap = marketData.marketCap?.number?.toLong(),
    marketCapRank = marketCapRank,
    priceChangePercentage24h = marketData.priceChangePercentage24h?.percentage,
)
