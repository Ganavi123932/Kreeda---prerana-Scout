package com.kreeda.prerana.data.db.dao

import androidx.room.*
import com.kreeda.prerana.data.db.entity.Performance
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Performance entity.
 * Handles logging, retrieval, and aggregation of athletic trial records.
 */
@Dao
interface PerformanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(performance: Performance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(performances: List<Performance>): List<Long>

    @Update
    suspend fun update(performance: Performance)

    @Delete
    suspend fun delete(performance: Performance)

    @Query("DELETE FROM performances WHERE id = :performanceId")
    suspend fun deleteById(performanceId: Long)

    @Query("""
        SELECT * FROM performances 
        WHERE athleteId = :athleteId 
        ORDER BY timestamp DESC
    """)
    fun getPerformancesForAthlete(athleteId: Long): Flow<List<Performance>>

    @Query("""
        SELECT * FROM performances 
        WHERE athleteId = :athleteId AND eventTypeId = :eventTypeId
        ORDER BY timestamp ASC
    """)
    fun getPerformancesForAthleteEvent(
        athleteId: Long,
        eventTypeId: Long
    ): Flow<List<Performance>>

    @Query("""
        SELECT * FROM performances 
        WHERE athleteId = :athleteId AND eventTypeId = :eventTypeId
        ORDER BY timestamp ASC
    """)
    suspend fun getPerformancesForAthleteEventSync(
        athleteId: Long,
        eventTypeId: Long
    ): List<Performance>

    @Query("""
        SELECT MIN(value) FROM performances 
        WHERE athleteId = :athleteId AND eventTypeId = :eventTypeId
    """)
    suspend fun getBestPerformanceLower(athleteId: Long, eventTypeId: Long): Double?

    @Query("""
        SELECT MAX(value) FROM performances 
        WHERE athleteId = :athleteId AND eventTypeId = :eventTypeId
    """)
    suspend fun getBestPerformanceHigher(athleteId: Long, eventTypeId: Long): Double?

    @Query("""
        SELECT * FROM performances 
        WHERE eventTypeId = :eventTypeId
        ORDER BY value ASC
    """)
    fun getAllPerformancesForEventAsc(eventTypeId: Long): Flow<List<Performance>>

    @Query("""
        SELECT * FROM performances 
        WHERE eventTypeId = :eventTypeId
        ORDER BY value DESC
    """)
    fun getAllPerformancesForEventDesc(eventTypeId: Long): Flow<List<Performance>>

    @Query("SELECT COUNT(*) FROM performances WHERE athleteId = :athleteId")
    fun getPerformanceCountForAthlete(athleteId: Long): Flow<Int>

    @Query("""
        SELECT DISTINCT eventTypeId FROM performances 
        WHERE athleteId = :athleteId
    """)
    suspend fun getEventTypesForAthlete(athleteId: Long): List<Long>

    @Query("""
        SELECT * FROM performances 
        WHERE eventTypeId = :eventTypeId 
        ORDER BY 
            CASE WHEN :ascending = 1 THEN value END ASC,
            CASE WHEN :ascending = 0 THEN value END DESC
        LIMIT 1
    """)
    suspend fun getTopPerformance(eventTypeId: Long, ascending: Boolean): Performance?
}
