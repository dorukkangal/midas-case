package com.midas.features.detail.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("description")
    val description: DescriptionResponse,
    @SerialName("image")
    val image: CoinImageResponse,
    @SerialName("market_cap_rank")
    val marketCapRank: Int?,
    @SerialName("market_data")
    val marketData: MarketDataResponse,
    @SerialName("categories")
    val categories: List<String>,
    @SerialName("genesis_date")
    val genesisDate: String?
)

@Serializable
data class DescriptionResponse(
    @SerialName("en")
    val en: String
)

@Serializable
data class CoinImageResponse(
    @SerialName("thumb")
    val thumb: String,
    @SerialName("small")
    val small: String,
    @SerialName("large")
    val large: String
)

@Serializable
data class MarketDataResponse(
    @SerialName("current_price")
    val currentPrice: Map<String, Double>,
    @SerialName("market_cap")
    val marketCap: Map<String, Long>,
    @SerialName("total_volume")
    val totalVolume: Map<String, Long>,
    @SerialName("high_24h")
    val high24h: Map<String, Double>,
    @SerialName("low_24h")
    val low24h: Map<String, Double>,
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,
    @SerialName("price_change_percentage_7d")
    val priceChangePercentage7d: Double?,
    @SerialName("price_change_percentage_30d")
    val priceChangePercentage30d: Double?,
    @SerialName("circulating_supply")
    val circulatingSupply: Double?,
    @SerialName("total_supply")
    val totalSupply: Double?,
    @SerialName("max_supply")
    val maxSupply: Double?
)
