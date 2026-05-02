package com.kreeda.prerana.data.repository

import com.kreeda.prerana.data.db.dao.BenchmarkDao
import com.kreeda.prerana.data.db.entity.Benchmark
import com.kreeda.prerana.data.db.entity.EventType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Benchmark and EventType data operations.
 * Provides benchmark thresholds for badge evaluation and chart overlays.
 */
@Singleton
class BenchmarkRepository @Inject constructor(
    private val benchmarkDao: BenchmarkDao
) {
    // --- EventType ---
    suspend fun insertEventType(eventType: EventType): Long =
        benchmarkDao.insertEventType(eventType)

    suspend fun insertEventTypes(eventTypes: List<EventType>): List<Long> =
        benchmarkDao.insertEventTypes(eventTypes)

    fun getAllEventTypes(): Flow<List<EventType>> =
        benchmarkDao.getAllEventTypes()

    suspend fun getEventTypeById(id: Long): EventType? =
        benchmarkDao.getEventTypeById(id)

    fun getEventTypesByCategory(category: String): Flow<List<EventType>> =
        benchmarkDao.getEventTypesByCategory(category)

    // --- Benchmark ---
    suspend fun insertBenchmark(benchmark: Benchmark): Long =
        benchmarkDao.insertBenchmark(benchmark)

    suspend fun insertBenchmarks(benchmarks: List<Benchmark>): List<Long> =
        benchmarkDao.insertBenchmarks(benchmarks)

    suspend fun getBenchmarksForEvent(
        eventTypeId: Long,
        gender: String,
        ageGroup: String
    ): List<Benchmark> =
        benchmarkDao.getBenchmarksForEvent(eventTypeId, gender, ageGroup)

    suspend fun getBenchmark(
        eventTypeId: Long,
        gender: String,
        ageGroup: String,
        level: String
    ): Benchmark? =
        benchmarkDao.getBenchmark(eventTypeId, gender, ageGroup, level)

    fun getBenchmarksByEvent(eventTypeId: Long): Flow<List<Benchmark>> =
        benchmarkDao.getBenchmarksByEvent(eventTypeId)

    fun getAllAgeGroups(): Flow<List<String>> =
        benchmarkDao.getAllAgeGroups()
}
