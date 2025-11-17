package com.midas.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Sizing(
    val spaceXSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceMediumLarge: Dp = 24.dp,
    val spaceLarge: Dp = 32.dp,
    val spaceXLarge: Dp = 64.dp,

    // Additional useful sizes
    val iconSizeSmall: Dp = 16.dp,
    val iconSizeMedium: Dp = 24.dp,
    val iconSizeLarge: Dp = 48.dp,

    val cardElevation: Dp = 2.dp,
    val cardElevationHighlight: Dp = 4.dp,

    val borderRadiusSmall: Dp = 4.dp,
    val borderRadiusMedium: Dp = 8.dp,
    val borderRadiusLarge: Dp = 16.dp,
)

val LocalSizing = compositionLocalOf { Sizing() }

val MaterialTheme.sizing: Sizing
    @Composable
    @ReadOnlyComposable
    get() = LocalSizing.current
