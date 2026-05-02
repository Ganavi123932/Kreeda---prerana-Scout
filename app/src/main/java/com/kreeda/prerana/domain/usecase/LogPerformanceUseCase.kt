package com.kreeda.prerana.domain.usecase

import com.kreeda.prerana.data.db.entity.Performance
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.data.repository.PerformanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for logging and managing athletic performance records.
 * Handles single entries and batch entries for PE sessions.
 */
class LogPerformanceUseCase @Inject constructor(
    private val performanceRepository: PerformanceRepository,
    private val benchmarkRepository: BenchmarkRepository
) {
    /**
     * Log a single performance entry.
     */
    suspend fun logPerformance(
        athleteId: Long,
        eventTypeId: Long,
        value: Double,
        notes: String? = null
    ): Long {
        val performance = Performance(
            athleteId = athleteId,
            eventTypeId = eventTypeId,
            value = value,
            notes = notes
        )
        return performanceRepository.logPerformance(performance)
    }

    /**
     * Log multiple performance entries (batch entry mode).
     */
    suspend fun logBatchPerformances(
        entries: List<Triple<Long, Long, Double>> // (athleteId, eventTypeId, value)
    ): List<Long> {
        val performances = entries.map { (athleteId, eventTypeId, value) ->
            Performance(
                athleteId = athleteId,
                eventTypeId = eventTypeId,
                value = value
            )
        }
        return performanceRepository.logPerformances(performances)
    }

    fun getPerformancesForAthlete(athleteId: Long): Flow<List<Performance>> =
        performanceRepository.getPerformancesForAthlete(athleteId)

    fun getPerformancesForAthleteEvent(
        athleteId: Long,
        eventTypeId: Long
    ): Flow<List<Performance>> =
        performanceRepository.getPerformancesForAthleteEvent(athleteId, eventTypeId)

    suspend fun updatePerformance(performance: Performance) =
        performanceRepository.updatePerformance(performance)

    suspend fun deletePerformance(performance: Performance) =
        performanceRepository.deletePerformance(performance)

    suspend fun deletePerformanceById(id: Long) =
        performanceRepository.deletePerformanceById(id)

    suspend fun getBestPerformance(
        athleteId: Long,
        eventTypeId: Long,
        higherIsBetter: Boolean
    ): Double? =
        performanceRepository.getBestPerformance(athleteId, eventTypeId, higherIsBetter)
}
