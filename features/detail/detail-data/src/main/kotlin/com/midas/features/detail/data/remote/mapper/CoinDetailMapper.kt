package com.midas.features.detail.data.remote.mapper

import com.midas.features.detail.data.remote.model.CoinDetailResponse
import com.midas.features.detail.data.remote.model.DescriptionResponse
import com.midas.features.detail.data.remote.model.MarketDataResponse
import com.midas.features.detail.domain.model.CoinDetail
import com.midas.features.detail.domain.model.MarketData
import java.util.Currency

fun CoinDetailResponse.toCoinDetail() = CoinDetail(
    id = id,
    name = name,
    symbol = symbol,
    description = description.toDescription(),
    image = image.large,
    marketCapRank = marketCapRank,
    marketData = marketData.toMarketData(),
    categories = categories,
    genesisDate = genesisDate,
)

fun DescriptionResponse.toDescription() = en
    .replace(Regex("<[^>]*>"), "")
    .replace(Regex("\\s+"), " ")
    .trim()
    .takeIf { it.isNotBlank() }

fun MarketDataResponse.toMarketData(
    currency: Currency = Currency.getInstance("USD")
) = MarketData(
    currentPrice = currentPrice[currency.currencyCode],
    marketCap = marketCap[currency.currencyCode],
    totalVolume = totalVolume[currency.currencyCode],
    high24h = high24h[currency.currencyCode],
    low24h = low24h[currency.currencyCode],
    priceChangePercentage24h = priceChangePercentage24h,
    priceChangePercentage7d = priceChangePercentage7d,
    priceChangePercentage30d = priceChangePercentage30d,
    circulatingSupply = circulatingSupply,
    totalSupply = totalSupply,
    maxSupply = maxSupply,
)
