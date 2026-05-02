package com.kreeda.prerana.domain.usecase

import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.repository.AthleteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for managing athlete profiles (CRUD + search/filter).
 */
class ManageAthleteUseCase @Inject constructor(
    private val athleteRepository: AthleteRepository
) {
    fun getAllAthletes(): Flow<List<Athlete>> =
        athleteRepository.getAllAthletes()

    fun getAthlete(id: Long): Flow<Athlete?> =
        athleteRepository.getAthleteById(id)

    fun searchAthletes(query: String): Flow<List<Athlete>> =
        athleteRepository.searchAthletes(query)

    fun filterAthletes(
        sport: String? = null,
        gender: String? = null,
        studentClass: String? = null
    ): Flow<List<Athlete>> =
        athleteRepository.filterAthletes(sport, gender, studentClass)

    suspend fun createAthlete(
        name: String,
        age: Int,
        gender: String,
        school: String,
        studentClass: String,
        primarySport: String,
        photoUri: String? = null
    ): Long {
        val athlete = Athlete(
            name = name,
            age = age,
            gender = gender,
            school = school,
            studentClass = studentClass,
            primarySport = primarySport,
            photoUri = photoUri
        )
        return athleteRepository.insertAthlete(athlete)
    }

    suspend fun updateAthlete(athlete: Athlete) =
        athleteRepository.updateAthlete(athlete)

    suspend fun deleteAthlete(athlete: Athlete) =
        athleteRepository.deleteAthlete(athlete)

    suspend fun deleteAthleteById(id: Long) =
        athleteRepository.deleteAthleteById(id)

    fun getAthleteCount(): Flow<Int> =
        athleteRepository.getAthleteCount()

    fun getAllSports(): Flow<List<String>> =
        athleteRepository.getAllSports()

    fun getAllSchools(): Flow<List<String>> =
        athleteRepository.getAllSchools()

    fun getAllClasses(): Flow<List<String>> =
        athleteRepository.getAllClasses()
}
