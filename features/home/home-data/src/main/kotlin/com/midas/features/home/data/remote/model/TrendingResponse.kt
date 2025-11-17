package com.midas.features.home.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendingResponse(
    @SerialName("coins")
    val coins: List<TrendingCoinWrapper>
)

@Serializable
data class TrendingCoinWrapper(
    @SerialName("item")
    val item: TrendingCoin
)

@Serializable
data class TrendingCoin(
    @SerialName("id")
    val id: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("name")
    val name: String,
    @SerialName("small")
    val image: String,
    @SerialName("market_cap_rank")
    val marketCapRank: Int?,
)
