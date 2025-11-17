package com.midas.features.home.domain.model

data class Coin(
    val id: String,
    val name: String,
    val symbol: String,
    val image: String,
    val currentPrice: Double?,
    val marketCap: Long?,
    val marketCapRank: Int?,
    val priceChangePercentage24h: Double?,
)
