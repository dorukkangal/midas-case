package com.midas.core.ui.mapper

import androidx.compose.ui.graphics.Color
import com.midas.core.ui.model.PercentageUiModel

fun Double.toPercentageUiModel() = PercentageUiModel(
    percentage = this,
    displayPercentage = "${if (this >= 0) "+" else ""}${"%.2f".format(this)}%",
    changeColor = if (this >= 0) Color.Green else Color.Red
)
