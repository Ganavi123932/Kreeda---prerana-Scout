package com.kreeda.prerana.data.repository

import com.kreeda.prerana.data.db.dao.AthleteDao
import com.kreeda.prerana.data.db.entity.Athlete
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Athlete data operations.
 * Serves as single source of truth for athlete profiles.
 */
@Singleton
class AthleteRepository @Inject constructor(
    private val athleteDao: AthleteDao
) {
    fun getAllAthletes(): Flow<List<Athlete>> = athleteDao.getAllAthletes()

    fun getAthleteById(id: Long): Flow<Athlete?> = athleteDao.getAthleteById(id)

    suspend fun getAthleteByIdSync(id: Long): Athlete? = athleteDao.getAthleteByIdSync(id)

    fun searchAthletes(query: String): Flow<List<Athlete>> = athleteDao.searchAthletes(query)

    fun filterAthletes(
        sport: String? = null,
        gender: String? = null,
        studentClass: String? = null
    ): Flow<List<Athlete>> = athleteDao.filterAthletes(sport, gender, studentClass)

    suspend fun insertAthlete(athlete: Athlete): Long = athleteDao.insert(athlete)

    suspend fun insertAthletes(athletes: List<Athlete>): List<Long> = athleteDao.insertAll(athletes)

    suspend fun updateAthlete(athlete: Athlete) {
        athleteDao.update(athlete.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteAthlete(athlete: Athlete) = athleteDao.delete(athlete)

    suspend fun deleteAthleteById(id: Long) = athleteDao.deleteById(id)

    fun getAthleteCount(): Flow<Int> = athleteDao.getAthleteCount()

    fun getAllSports(): Flow<List<String>> = athleteDao.getAllSports()

    fun getAllSchools(): Flow<List<String>> = athleteDao.getAllSchools()

    fun getAllClasses(): Flow<List<String>> = athleteDao.getAllClasses()
}
