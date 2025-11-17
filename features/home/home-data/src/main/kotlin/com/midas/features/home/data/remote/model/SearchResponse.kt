package com.midas.features.home.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    @SerialName("coins")
    val coins: List<SearchCoin>
)

@Serializable
data class SearchCoin(
    @SerialName("id")
    val id: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("name")
    val name: String,
    @SerialName("thumb")
    val image: String,
    @SerialName("market_cap_rank")
    val marketCapRank: Int?,
)
