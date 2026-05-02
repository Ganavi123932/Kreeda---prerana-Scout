package com.kreeda.prerana.domain.model

import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.db.entity.Badge

/**
 * Composite model combining an Athlete with their aggregate statistics.
 * Used for display in athlete cards and profile screens.
 */
data class AthleteWithStats(
    val athlete: Athlete,
    val totalPerformances: Int = 0,
    val totalBadges: Int = 0,
    val badges: List<Badge> = emptyList(),
    val bestPerformances: Map<Long, Double> = emptyMap() // eventTypeId -> best value
)
