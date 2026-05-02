package com.kreeda.prerana.data.db.dao

import androidx.room.*
import com.kreeda.prerana.data.db.entity.Athlete
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Athlete entity.
 * Provides CRUD operations and search/filter queries.
 */
@Dao
interface AthleteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(athlete: Athlete): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(athletes: List<Athlete>): List<Long>

    @Update
    suspend fun update(athlete: Athlete)

    @Delete
    suspend fun delete(athlete: Athlete)

    @Query("DELETE FROM athletes WHERE id = :athleteId")
    suspend fun deleteById(athleteId: Long)

    @Query("SELECT * FROM athletes ORDER BY name ASC")
    fun getAllAthletes(): Flow<List<Athlete>>

    @Query("SELECT * FROM athletes WHERE id = :athleteId")
    fun getAthleteById(athleteId: Long): Flow<Athlete?>

    @Query("SELECT * FROM athletes WHERE id = :athleteId")
    suspend fun getAthleteByIdSync(athleteId: Long): Athlete?

    @Query("""
        SELECT * FROM athletes 
        WHERE name LIKE '%' || :query || '%' 
        OR school LIKE '%' || :query || '%'
        OR primarySport LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchAthletes(query: String): Flow<List<Athlete>>

    @Query("""
        SELECT * FROM athletes 
        WHERE (:sport IS NULL OR primarySport = :sport)
        AND (:gender IS NULL OR gender = :gender)
        AND (:studentClass IS NULL OR studentClass = :studentClass)
        ORDER BY name ASC
    """)
    fun filterAthletes(
        sport: String? = null,
        gender: String? = null,
        studentClass: String? = null
    ): Flow<List<Athlete>>

    @Query("SELECT COUNT(*) FROM athletes")
    fun getAthleteCount(): Flow<Int>

    @Query("SELECT DISTINCT primarySport FROM athletes ORDER BY primarySport ASC")
    fun getAllSports(): Flow<List<String>>

    @Query("SELECT DISTINCT school FROM athletes ORDER BY school ASC")
    fun getAllSchools(): Flow<List<String>>

    @Query("SELECT DISTINCT studentClass FROM athletes ORDER BY studentClass ASC")
    fun getAllClasses(): Flow<List<String>>
}
