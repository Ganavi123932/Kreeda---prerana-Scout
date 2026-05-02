package com.kreeda.prerana.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreeda.prerana.ui.util.FormatUtils

/**
 * Large chronometer display showing elapsed time in MM:SS.CC format.
 * Uses monospace font for stable digit alignment during counting.
 */
@Composable
fun ChronoDisplay(
    elapsedMillis: Long,
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedTime = FormatUtils.formatChronoTime(elapsedMillis)

    // Subtle pulse animation when running
    val pulseAlpha by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0.85f,
        animationSpec = if (isRunning) {
            infiniteRepeatable(
                animation = tween(800, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(300)
        },
        label = "chronoPulse"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status indicator
        Text(
            text = when {
                isRunning -> "⏱ Running..."
                elapsedMillis > 0 -> "⏸ Stopped"
                else -> "Ready"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
