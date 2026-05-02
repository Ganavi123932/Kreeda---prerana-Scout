package com.kreeda.prerana.data.db.dao

import androidx.room.*
import com.kreeda.prerana.data.db.entity.Benchmark
import com.kreeda.prerana.data.db.entity.EventType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Benchmark and EventType entities.
 * Used for badge evaluation and benchmark overlay on talent curves.
 */
@Dao
interface BenchmarkDao {

    // --- EventType operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventType(eventType: EventType): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventTypes(eventTypes: List<EventType>): List<Long>

    @Query("SELECT * FROM event_types ORDER BY category, name ASC")
    fun getAllEventTypes(): Flow<List<EventType>>

    @Query("SELECT * FROM event_types WHERE id = :eventTypeId")
    suspend fun getEventTypeById(eventTypeId: Long): EventType?

    @Query("SELECT * FROM event_types WHERE category = :category ORDER BY name ASC")
    fun getEventTypesByCategory(category: String): Flow<List<EventType>>

    // --- Benchmark operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBenchmark(benchmark: Benchmark): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBenchmarks(benchmarks: List<Benchmark>): List<Long>

    @Query("""
        SELECT * FROM benchmarks 
        WHERE eventTypeId = :eventTypeId 
        AND gender = :gender 
        AND ageGroup = :ageGroup
        ORDER BY threshold ASC
    """)
    suspend fun getBenchmarksForEvent(
        eventTypeId: Long,
        gender: String,
        ageGroup: String
    ): List<Benchmark>

    @Query("""
        SELECT * FROM benchmarks 
        WHERE eventTypeId = :eventTypeId 
        AND gender = :gender 
        AND ageGroup = :ageGroup
        AND level = :level
    """)
    suspend fun getBenchmark(
        eventTypeId: Long,
        gender: String,
        ageGroup: String,
        level: String
    ): Benchmark?

    @Query("SELECT * FROM benchmarks WHERE eventTypeId = :eventTypeId")
    fun getBenchmarksByEvent(eventTypeId: Long): Flow<List<Benchmark>>

    @Query("SELECT DISTINCT ageGroup FROM benchmarks ORDER BY ageGroup ASC")
    fun getAllAgeGroups(): Flow<List<String>>
}
