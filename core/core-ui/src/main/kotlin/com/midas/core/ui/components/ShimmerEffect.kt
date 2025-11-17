package com.midas.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.midas.core.ui.theme.sizing

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Box(
        modifier = modifier.background(brush)
    )
}

@Composable
fun CoinListItemSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.sizing.cardElevation
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.sizing.spaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.sizing.spaceSmall
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image placeholder
                ShimmerEffect(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        MaterialTheme.sizing.spaceSmall
                    )
                ) {
                    // Name placeholder
                    ShimmerEffect(
                        modifier = Modifier
                            .width(120.dp)
                            .height(MaterialTheme.sizing.spaceMedium)
                            .clip(
                                RoundedCornerShape(
                                    MaterialTheme.sizing.spaceXSmall
                                )
                            )
                    )
                    // Symbol placeholder
                    ShimmerEffect(
                        modifier = Modifier
                            .width(60.dp)
                            .height(12.dp)
                            .clip(
                                RoundedCornerShape(
                                    MaterialTheme.sizing.spaceXSmall
                                )
                            )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.sizing.spaceSmall
                )
            ) {
                // Price placeholder
                ShimmerEffect(
                    modifier = Modifier
                        .width(80.dp)
                        .height(MaterialTheme.sizing.spaceMedium)
                        .clip(
                            RoundedCornerShape(
                                MaterialTheme.sizing.spaceXSmall
                            )
                        )
                )
                // Change placeholder
                ShimmerEffect(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp)
                        .clip(
                            RoundedCornerShape(
                                MaterialTheme.sizing.spaceXSmall
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun TrendingCoinCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(140.dp)
            .height(120.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.sizing.cardElevation
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.sizing.spaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.sizing.spaceSmall
            )
        ) {
            // Image placeholder
            ShimmerEffect(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            // Name placeholder
            ShimmerEffect(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp)
                    .clip(
                        RoundedCornerShape(
                            MaterialTheme.sizing.spaceXSmall
                        )
                    )
            )

            // Symbol placeholder
            ShimmerEffect(
                modifier = Modifier
                    .width(50.dp)
                    .height(12.dp)
                    .clip(
                        RoundedCornerShape(
                            MaterialTheme.sizing.spaceXSmall
                        )
                    )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Price placeholder
            ShimmerEffect(
                modifier = Modifier
                    .width(70.dp)
                    .height(MaterialTheme.sizing.spaceMedium)
                    .clip(
                        RoundedCornerShape(
                            MaterialTheme.sizing.spaceXSmall
                        )
                    )
            )

            // Change placeholder
            ShimmerEffect(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .clip(
                        RoundedCornerShape(
                            MaterialTheme.sizing.spaceXSmall
                        )
                    )
            )
        }
    }
}
