package com.midas.core.ui.mapper

import com.midas.core.ui.model.AmountUiModel
import java.text.NumberFormat
import java.util.Currency

fun Double.toAmountUiModel(
    currency: Currency = Currency.getInstance("USD")
) = AmountUiModel(
    amount = this,
    currency = currency,
    displayAmount = NumberFormat
        .getCurrencyInstance()
        .apply { this.currency = currency }
        .format(this)
)
