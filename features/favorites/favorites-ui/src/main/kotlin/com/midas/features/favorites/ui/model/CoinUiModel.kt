package com.midas.features.favorites.ui.model

import com.midas.core.ui.model.AmountUiModel
import com.midas.core.ui.model.LargeNumberUiModel
import com.midas.core.ui.model.PercentageUiModel

data class CoinUiModel(
    val id: String,
    val name: String,
    val symbol: String,
    val image: String,
    val currentPrice: AmountUiModel?,
    val marketCap: LargeNumberUiModel?,
    val marketCapRank: Int?,
    val priceChangePercentage24h: PercentageUiModel?,
)
