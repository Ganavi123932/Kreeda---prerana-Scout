package com.kreeda.prerana.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kreeda.prerana.ui.theme.*

/**
 * Animated badge chip displaying the achievement level.
 * Shows bronze/silver/gold styling based on badge tier.
 */
@Composable
fun BadgeChip(
    level: String,
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    val (backgroundColor, textColor, emoji) = when (level) {
        "District" -> Triple(BadgeDistrict.copy(alpha = 0.15f), BadgeDistrict, "🥉")
        "State" -> Triple(BadgeState.copy(alpha = 0.2f), Color(0xFF757575), "🥈")
        "National" -> Triple(BadgeNational.copy(alpha = 0.15f), Color(0xFFB8860B), "🥇")
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "🏅"
        )
    }

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "badgeScale"
    )

    Surface(
        modifier = modifier.scale(if (animated) scale else 1f),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${level} Ready",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}
