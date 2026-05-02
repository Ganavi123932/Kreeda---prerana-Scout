package com.kreeda.prerana.domain.model

import com.kreeda.prerana.data.db.entity.Athlete

/**
 * Represents a single entry in the school leaderboard.
 * Contains the athlete, their best performance value, rank, and event info.
 */
data class LeaderboardEntry(
    val rank: Int,
    val athlete: Athlete,
    val eventTypeId: Long,
    val eventName: String,
    val bestValue: Double,
    val unit: String,
    val badgeLevel: String? = null // "District", "State", "National", or null
)
