package com.midas.core.ui.model

import java.util.Currency

data class AmountUiModel(
    val amount: Double,
    val currency: Currency? = null,
    val displayAmount: String
)
