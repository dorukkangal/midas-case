package com.midas.core.ui.extensions

import androidx.compose.foundation.lazy.LazyListState

val LazyListState.totalItemCount: Int
    get() = layoutInfo.totalItemsCount

val LazyListState.lastVisibleItemIndex: Int
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
