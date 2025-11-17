package com.midas.features.home.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("image")
    val image: String,
    @SerialName("current_price")
    val currentPrice: Double?,
    @SerialName("market_cap")
    val marketCap: Long?,
    @SerialName("market_cap_rank")
    val marketCapRank: Int?,
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,
)
