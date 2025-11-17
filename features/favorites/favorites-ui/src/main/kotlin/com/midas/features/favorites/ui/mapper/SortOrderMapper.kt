package com.midas.features.favorites.ui.mapper

import com.midas.features.favorites.ui.model.SortOrderUiModel
import com.midas.features.home.domain.model.SortOrder

fun SortOrder.toSortOrderUiModel() = when (this) {
    SortOrder.NAME_ASC -> SortOrderUiModel.NAME_ASC
    SortOrder.NAME_DESC -> SortOrderUiModel.NAME_DESC
    SortOrder.PRICE_ASC -> SortOrderUiModel.PRICE_ASC
    SortOrder.PRICE_DESC -> SortOrderUiModel.PRICE_DESC
    SortOrder.MARKET_CAP_ASC -> SortOrderUiModel.MARKET_CAP_ASC
    SortOrder.MARKET_CAP_DESC -> SortOrderUiModel.MARKET_CAP_DESC
    SortOrder.CHANGE_24H_ASC -> SortOrderUiModel.CHANGE_24H_ASC
    SortOrder.CHANGE_24H_DESC -> SortOrderUiModel.CHANGE_24H_DESC
}

fun SortOrderUiModel.toSortOrder() = when (this) {
    SortOrderUiModel.NAME_ASC -> SortOrder.NAME_ASC
    SortOrderUiModel.NAME_DESC -> SortOrder.NAME_DESC
    SortOrderUiModel.PRICE_ASC -> SortOrder.PRICE_ASC
    SortOrderUiModel.PRICE_DESC -> SortOrder.PRICE_DESC
    SortOrderUiModel.MARKET_CAP_ASC -> SortOrder.MARKET_CAP_ASC
    SortOrderUiModel.MARKET_CAP_DESC -> SortOrder.MARKET_CAP_DESC
    SortOrderUiModel.CHANGE_24H_ASC -> SortOrder.CHANGE_24H_ASC
    SortOrderUiModel.CHANGE_24H_DESC -> SortOrder.CHANGE_24H_DESC
}
