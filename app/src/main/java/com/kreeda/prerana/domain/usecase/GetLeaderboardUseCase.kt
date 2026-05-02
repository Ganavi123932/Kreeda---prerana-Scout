package com.kreeda.prerana.domain.usecase

import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.db.entity.Performance
import com.kreeda.prerana.data.repository.AthleteRepository
import com.kreeda.prerana.data.repository.BadgeRepository
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.data.repository.PerformanceRepository
import com.kreeda.prerana.domain.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for generating leaderboard rankings.
 * Supports filtering by sport, event, gender, and age group.
 * Uses efficient sorting for up to 1000+ athlete records.
 */
class GetLeaderboardUseCase @Inject constructor(
    private val performanceRepository: PerformanceRepository,
    private val athleteRepository: AthleteRepository,
    private val benchmarkRepository: BenchmarkRepository,
    private val badgeRepository: BadgeRepository
) {
    /**
     * Generate leaderboard for a specific event.
     * Returns ranked list of athletes sorted by best performance.
     */
    fun getLeaderboard(
        eventTypeId: Long,
        gender: String? = null,
        ageGroup: String? = null
    ): Flow<List<LeaderboardEntry>> = flow {
        val eventType = benchmarkRepository.getEventTypeById(eventTypeId)
            ?: run { emit(emptyList()); return@flow }

        val allPerformances = performanceRepository
            .getAllPerformancesForEvent(eventTypeId, !eventType.higherIsBetter)
            .first()

        // Group by athlete and get best performance
        val bestByAthlete = mutableMapOf<Long, Performance>()
        for (perf in allPerformances) {
            val current = bestByAthlete[perf.athleteId]
            if (current == null) {
                bestByAthlete[perf.athleteId] = perf
            } else {
                val isBetter = if (eventType.higherIsBetter) {
                    perf.value > current.value
                } else {
                    perf.value < current.value
                }
                if (isBetter) bestByAthlete[perf.athleteId] = perf
            }
        }

        // Sort by best performance
        val sorted = bestByAthlete.entries.toList().sortedWith(
            if (eventType.higherIsBetter) {
                compareByDescending { it.value.value }
            } else {
                compareBy { it.value.value }
            }
        )

        // Build leaderboard entries
        val entries = sorted.mapIndexedNotNull { index, entry ->
            val athlete = athleteRepository.getAthleteByIdSync(entry.key) ?: return@mapIndexedNotNull null

            // Apply gender filter
            if (gender != null && athlete.gender != gender) return@mapIndexedNotNull null

            // Get badge level
            val badgeLevel = getBadgeLevel(athlete.id, eventTypeId)

            LeaderboardEntry(
                rank = index + 1,
                athlete = athlete,
                eventTypeId = eventTypeId,
                eventName = eventType.name,
                bestValue = entry.value.value,
                unit = eventType.unit,
                badgeLevel = badgeLevel
            )
        }

        // Re-rank after filtering
        val reRanked = entries.mapIndexed { index, entry ->
            entry.copy(rank = index + 1)
        }

        emit(reRanked)
    }

    private suspend fun getBadgeLevel(athleteId: Long, eventTypeId: Long): String? {
        val levels = listOf("National", "State", "District")
        for (level in levels) {
            if (badgeRepository.hasBadge(athleteId, eventTypeId, level)) {
                return level
            }
        }
        return null
    }
}
