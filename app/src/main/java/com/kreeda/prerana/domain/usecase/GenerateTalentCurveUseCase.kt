package com.kreeda.prerana.domain.usecase

import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.data.repository.PerformanceRepository
import com.kreeda.prerana.domain.model.TalentCurveData
import com.kreeda.prerana.domain.model.TalentCurvePoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Use case for generating talent curve visualization data.
 * Produces time-series data points with benchmark overlay values
 * for rendering performance progression charts.
 */
class GenerateTalentCurveUseCase @Inject constructor(
    private val performanceRepository: PerformanceRepository,
    private val benchmarkRepository: BenchmarkRepository
) {
    private val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())

    /**
     * Generate talent curve data for an athlete's performance in a specific event.
     * Includes benchmark overlay lines for District/State/National levels.
     */
    suspend fun generateTalentCurve(
        athleteId: Long,
        athleteName: String,
        eventTypeId: Long,
        gender: String,
        ageGroup: String
    ): TalentCurveData? {
        val eventType = benchmarkRepository.getEventTypeById(eventTypeId) ?: return null

        val performances = performanceRepository
            .getPerformancesForAthleteEventSync(athleteId, eventTypeId)

        if (performances.isEmpty()) return null

        val dataPoints = performances.map { perf ->
            TalentCurvePoint(
                timestamp = perf.timestamp,
                value = perf.value,
                formattedDate = dateFormat.format(Date(perf.timestamp))
            )
        }

        // Get benchmark values for overlay
        val benchmarks = benchmarkRepository.getBenchmarksForEvent(
            eventTypeId, gender, ageGroup
        )

        return TalentCurveData(
            athleteName = athleteName,
            eventName = eventType.name,
            unit = eventType.unit,
            higherIsBetter = eventType.higherIsBetter,
            dataPoints = dataPoints,
            districtBenchmark = benchmarks.find { it.level == "District" }?.threshold,
            stateBenchmark = benchmarks.find { it.level == "State" }?.threshold,
            nationalBenchmark = benchmarks.find { it.level == "National" }?.threshold
        )
    }
}
