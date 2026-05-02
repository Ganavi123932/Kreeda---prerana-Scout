package com.kreeda.prerana.domain.model

/**
 * Data class representing a single point on the talent curve chart.
 * Contains the timestamp, performance value, and optional benchmark references.
 */
data class TalentCurvePoint(
    val timestamp: Long,
    val value: Double,
    val formattedDate: String
)

/**
 * Complete data set for rendering a talent curve chart.
 * Includes performance data points and benchmark overlay lines.
 */
data class TalentCurveData(
    val athleteName: String,
    val eventName: String,
    val unit: String,
    val higherIsBetter: Boolean,
    val dataPoints: List<TalentCurvePoint>,
    val districtBenchmark: Double? = null,
    val stateBenchmark: Double? = null,
    val nationalBenchmark: Double? = null
)
