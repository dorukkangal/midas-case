package com.midas.features.detail.ui.model

import com.midas.core.ui.model.AmountUiModel
import com.midas.core.ui.model.LargeNumberUiModel
import com.midas.core.ui.model.PercentageUiModel

data class CoinDetailUiModel(
    val id: String,
    val name: String,
    val symbol: String,
    val description: String?,
    val image: String,
    val marketCapRank: Int?,
    val marketData: MarketDataUiModel,
    val categories: List<String>,
    val genesisDate: String?
)

data class MarketDataUiModel(
    val currentPrice: AmountUiModel?,
    val marketCap: LargeNumberUiModel?,
    val totalVolume: LargeNumberUiModel?,
    val high24h: AmountUiModel?,
    val low24h: AmountUiModel?,
    val priceChangePercentage24h: PercentageUiModel?,
    val priceChangePercentage7d: PercentageUiModel?,
    val priceChangePercentage30d: PercentageUiModel?,
    val circulatingSupply: LargeNumberUiModel?,
    val totalSupply: LargeNumberUiModel?,
    val maxSupply: LargeNumberUiModel?
)
