package com.kreeda.prerana.data.repository

import com.kreeda.prerana.data.db.dao.PerformanceDao
import com.kreeda.prerana.data.db.entity.Performance
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Performance data operations.
 * Manages all trial/performance record CRUD and aggregation.
 */
@Singleton
class PerformanceRepository @Inject constructor(
    private val performanceDao: PerformanceDao
) {
    suspend fun logPerformance(performance: Performance): Long =
        performanceDao.insert(performance)

    suspend fun logPerformances(performances: List<Performance>): List<Long> =
        performanceDao.insertAll(performances)

    suspend fun updatePerformance(performance: Performance) =
        performanceDao.update(performance)

    suspend fun deletePerformance(performance: Performance) =
        performanceDao.delete(performance)

    suspend fun deletePerformanceById(id: Long) =
        performanceDao.deleteById(id)

    fun getPerformancesForAthlete(athleteId: Long): Flow<List<Performance>> =
        performanceDao.getPerformancesForAthlete(athleteId)

    fun getPerformancesForAthleteEvent(
        athleteId: Long,
        eventTypeId: Long
    ): Flow<List<Performance>> =
        performanceDao.getPerformancesForAthleteEvent(athleteId, eventTypeId)

    suspend fun getPerformancesForAthleteEventSync(
        athleteId: Long,
        eventTypeId: Long
    ): List<Performance> =
        performanceDao.getPerformancesForAthleteEventSync(athleteId, eventTypeId)

    suspend fun getBestPerformance(
        athleteId: Long,
        eventTypeId: Long,
        higherIsBetter: Boolean
    ): Double? {
        return if (higherIsBetter) {
            performanceDao.getBestPerformanceHigher(athleteId, eventTypeId)
        } else {
            performanceDao.getBestPerformanceLower(athleteId, eventTypeId)
        }
    }

    fun getAllPerformancesForEvent(
        eventTypeId: Long,
        ascending: Boolean
    ): Flow<List<Performance>> {
        return if (ascending) {
            performanceDao.getAllPerformancesForEventAsc(eventTypeId)
        } else {
            performanceDao.getAllPerformancesForEventDesc(eventTypeId)
        }
    }

    fun getPerformanceCountForAthlete(athleteId: Long): Flow<Int> =
        performanceDao.getPerformanceCountForAthlete(athleteId)

    suspend fun getEventTypesForAthlete(athleteId: Long): List<Long> =
        performanceDao.getEventTypesForAthlete(athleteId)
}
