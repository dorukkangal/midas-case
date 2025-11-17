package com.midas.features.favorites.ui.model

import androidx.annotation.StringRes
import com.midas.features.favorites.ui.R

enum class SortOrderUiModel(
    @param:StringRes val displayName: Int
) {
    NAME_ASC(R.string.sort_by_name_asc),
    NAME_DESC(R.string.sort_by_name_desc),
    PRICE_ASC(R.string.sort_by_price_asc),
    PRICE_DESC(R.string.sort_by_price_desc),
    MARKET_CAP_ASC(R.string.sort_by_market_cap_asc),
    MARKET_CAP_DESC(R.string.sort_by_market_cap_desc),
    CHANGE_24H_ASC(R.string.sort_by_change_24h_asc),
    CHANGE_24H_DESC(R.string.sort_by_change_24h_desc),
}
