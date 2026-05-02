package com.kreeda.prerana.domain.usecase

import com.kreeda.prerana.data.db.entity.Badge
import com.kreeda.prerana.data.repository.BadgeRepository
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.data.repository.PerformanceRepository
import javax.inject.Inject

/**
 * Use case for evaluating and awarding milestone badges.
 * Automatically checks performance against benchmark thresholds
 * after each performance entry.
 *
 * Badge levels (in order): District → State → National
 * For timed events (higherIsBetter=false): athlete must score LESS THAN OR EQUAL to threshold
 * For distance events (higherIsBetter=true): athlete must score GREATER THAN OR EQUAL to threshold
 */
class EvaluateBadgeUseCase @Inject constructor(
    private val badgeRepository: BadgeRepository,
    private val benchmarkRepository: BenchmarkRepository,
    private val performanceRepository: PerformanceRepository
) {
    private val badgeLevels = listOf("District", "State", "National")

    /**
     * Evaluate badges for a specific athlete and event after a new performance.
     * Returns list of newly awarded badges (may be empty).
     */
    suspend fun evaluateBadges(
        athleteId: Long,
        eventTypeId: Long,
        gender: String,
        ageGroup: String
    ): List<Badge> {
        val eventType = benchmarkRepository.getEventTypeById(eventTypeId) ?: return emptyList()
        val bestPerformance = performanceRepository.getBestPerformance(
            athleteId, eventTypeId, eventType.higherIsBetter
        ) ?: return emptyList()

        val benchmarks = benchmarkRepository.getBenchmarksForEvent(
            eventTypeId, gender, ageGroup
        )

        val newBadges = mutableListOf<Badge>()

        for (level in badgeLevels) {
            val benchmark = benchmarks.find { it.level == level } ?: continue

            // Check if athlete already has this badge
            if (badgeRepository.hasBadge(athleteId, eventTypeId, level)) continue

            // Evaluate: for timed events, lower is better
            val meetsThreshold = if (eventType.higherIsBetter) {
                bestPerformance >= benchmark.threshold
            } else {
                bestPerformance <= benchmark.threshold
            }

            if (meetsThreshold) {
                val badge = Badge(
                    athleteId = athleteId,
                    eventTypeId = eventTypeId,
                    level = level
                )
                badgeRepository.awardBadge(badge)
                newBadges.add(badge)
            }
        }

        return newBadges
    }

    /**
     * Get the highest badge level achieved for an athlete-event combination.
     */
    suspend fun getHighestBadgeLevel(
        athleteId: Long,
        eventTypeId: Long
    ): String? {
        for (level in badgeLevels.reversed()) {
            if (badgeRepository.hasBadge(athleteId, eventTypeId, level)) {
                return level
            }
        }
        return null
    }
}
