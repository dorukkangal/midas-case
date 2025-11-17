package com.midas.core.ui.mapper

import com.midas.core.ui.model.LargeNumberUiModel

fun Double.toLargeNumberUiModel() = LargeNumberUiModel(
    number = this,
    displayNumber = when {
        this >= 1_000_000_000_000 -> "${"%.2f".format(this / 1_000_000_000_000)}T"
        this >= 1_000_000_000 -> "${"%.2f".format(this / 1_000_000_000)}B"
        this >= 1_000_000 -> "${"%.2f".format(this / 1_000_000)}M"
        this >= 1_000 -> "${"%.2f".format(this / 1_000)}K"
        else -> "%.2f".format(this)
    }
)
