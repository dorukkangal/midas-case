package com.midas.features.detail.domain.model

data class CoinDetail(
    val id: String,
    val name: String,
    val symbol: String,
    val description: String?,
    val image: String,
    val marketCapRank: Int?,
    val marketData: MarketData,
    val categories: List<String>,
    val genesisDate: String?
)

data class MarketData(
    val currentPrice: Double?,
    val marketCap: Long?,
    val totalVolume: Long?,
    val high24h: Double?,
    val low24h: Double?,
    val priceChangePercentage24h: Double?,
    val priceChangePercentage7d: Double?,
    val priceChangePercentage30d: Double?,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val maxSupply: Double?
)
