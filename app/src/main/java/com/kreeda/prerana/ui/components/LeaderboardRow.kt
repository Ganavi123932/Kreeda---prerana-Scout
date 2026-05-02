package com.kreeda.prerana.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.ui.theme.*
import com.kreeda.prerana.ui.util.FormatUtils

/**
 * Leaderboard row with rank indicator, athlete info, and performance value.
 * Top 3 ranks get special gold/silver/bronze styling.
 */
@Composable
fun LeaderboardRow(
    rank: Int,
    athleteName: String,
    value: Double,
    unit: String,
    badgeLevel: String? = null,
    modifier: Modifier = Modifier
) {
    val rankColor = when (rank) {
        1 -> RankGold
        2 -> RankSilver
        3 -> RankBronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val rankEmoji = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (rank <= 3) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            rankColor.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Box(
            modifier = Modifier.width(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (rankEmoji != null) {
                Text(
                    text = rankEmoji,
                    style = MaterialTheme.typography.titleLarge
                )
            } else {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Athlete name
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = athleteName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (rank <= 3) FontWeight.SemiBold else FontWeight.Normal
            )
            if (badgeLevel != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${FormatUtils.getBadgeEmoji(badgeLevel)} ${badgeLevel} Ready",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Performance value
        Text(
            text = FormatUtils.formatPerformanceValue(value, unit),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
