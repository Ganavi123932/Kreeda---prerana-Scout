package com.kreeda.prerana.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.db.entity.Badge
import com.kreeda.prerana.data.db.entity.Benchmark
import com.kreeda.prerana.data.db.entity.EventType
import com.kreeda.prerana.data.repository.BadgeRepository
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.domain.usecase.ManageAthleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val athleteCount: Int = 0,
    val recentBadges: List<Badge> = emptyList(),
    val isLoading: Boolean = true,
    val sportsCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val manageAthleteUseCase: ManageAthleteUseCase,
    private val badgeRepository: BadgeRepository,
    private val benchmarkRepository: BenchmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        seedDataIfNeeded()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            manageAthleteUseCase.getAthleteCount().collect { count ->
                _uiState.update { it.copy(athleteCount = count, isLoading = false) }
            }
        }
        viewModelScope.launch {
            badgeRepository.getRecentBadges(5).collect { badges ->
                _uiState.update { it.copy(recentBadges = badges) }
            }
        }
        viewModelScope.launch {
            manageAthleteUseCase.getAllSports().collect { sports ->
                _uiState.update { it.copy(sportsCount = sports.size) }
            }
        }
    }

    /**
     * Seed event types and benchmarks on first launch.
     */
    private fun seedDataIfNeeded() {
        viewModelScope.launch {
            val existingTypes = benchmarkRepository.getAllEventTypes().first()
            if (existingTypes.isNotEmpty()) return@launch

            // Seed event types
            val eventTypes = listOf(
                EventType(id = 1, name = "100m Sprint", category = "Track", unit = "seconds", higherIsBetter = false),
                EventType(id = 2, name = "200m Sprint", category = "Track", unit = "seconds", higherIsBetter = false),
                EventType(id = 3, name = "400m Sprint", category = "Track", unit = "seconds", higherIsBetter = false),
                EventType(id = 4, name = "800m Run", category = "Track", unit = "seconds", higherIsBetter = false),
                EventType(id = 5, name = "1500m Run", category = "Track", unit = "seconds", higherIsBetter = false),
                EventType(id = 6, name = "Long Jump", category = "Field", unit = "meters", higherIsBetter = true),
                EventType(id = 7, name = "High Jump", category = "Field", unit = "meters", higherIsBetter = true),
                EventType(id = 8, name = "Shot Put", category = "Field", unit = "meters", higherIsBetter = true),
                EventType(id = 9, name = "Discus Throw", category = "Field", unit = "meters", higherIsBetter = true),
                EventType(id = 10, name = "Javelin Throw", category = "Field", unit = "meters", higherIsBetter = true),
                EventType(id = 11, name = "Kabaddi Raid Points", category = "Sport-Specific", unit = "points", higherIsBetter = true),
                EventType(id = 12, name = "Kho-Kho Chase Time", category = "Sport-Specific", unit = "seconds", higherIsBetter = false)
            )
            benchmarkRepository.insertEventTypes(eventTypes)

            // Seed benchmarks (U-17 age group — primary target)
            val benchmarks = listOf(
                // 100m Sprint - Male
                Benchmark(eventTypeId = 1, level = "District", gender = "Male", ageGroup = "U-14", threshold = 14.0),
                Benchmark(eventTypeId = 1, level = "State", gender = "Male", ageGroup = "U-14", threshold = 13.0),
                Benchmark(eventTypeId = 1, level = "National", gender = "Male", ageGroup = "U-14", threshold = 12.0),
                Benchmark(eventTypeId = 1, level = "District", gender = "Male", ageGroup = "U-17", threshold = 13.5),
                Benchmark(eventTypeId = 1, level = "State", gender = "Male", ageGroup = "U-17", threshold = 12.5),
                Benchmark(eventTypeId = 1, level = "National", gender = "Male", ageGroup = "U-17", threshold = 11.5),
                Benchmark(eventTypeId = 1, level = "District", gender = "Male", ageGroup = "U-19", threshold = 12.5),
                Benchmark(eventTypeId = 1, level = "State", gender = "Male", ageGroup = "U-19", threshold = 11.8),
                Benchmark(eventTypeId = 1, level = "National", gender = "Male", ageGroup = "U-19", threshold = 11.0),
                // 100m Sprint - Female
                Benchmark(eventTypeId = 1, level = "District", gender = "Female", ageGroup = "U-14", threshold = 15.5),
                Benchmark(eventTypeId = 1, level = "State", gender = "Female", ageGroup = "U-14", threshold = 14.5),
                Benchmark(eventTypeId = 1, level = "National", gender = "Female", ageGroup = "U-14", threshold = 13.5),
                Benchmark(eventTypeId = 1, level = "District", gender = "Female", ageGroup = "U-17", threshold = 14.5),
                Benchmark(eventTypeId = 1, level = "State", gender = "Female", ageGroup = "U-17", threshold = 13.5),
                Benchmark(eventTypeId = 1, level = "National", gender = "Female", ageGroup = "U-17", threshold = 12.5),
                // 200m Sprint - Male
                Benchmark(eventTypeId = 2, level = "District", gender = "Male", ageGroup = "U-17", threshold = 27.0),
                Benchmark(eventTypeId = 2, level = "State", gender = "Male", ageGroup = "U-17", threshold = 25.0),
                Benchmark(eventTypeId = 2, level = "National", gender = "Male", ageGroup = "U-17", threshold = 23.5),
                // 400m Sprint - Male
                Benchmark(eventTypeId = 3, level = "District", gender = "Male", ageGroup = "U-17", threshold = 60.0),
                Benchmark(eventTypeId = 3, level = "State", gender = "Male", ageGroup = "U-17", threshold = 55.0),
                Benchmark(eventTypeId = 3, level = "National", gender = "Male", ageGroup = "U-17", threshold = 52.0),
                // Long Jump - Male
                Benchmark(eventTypeId = 6, level = "District", gender = "Male", ageGroup = "U-14", threshold = 4.0),
                Benchmark(eventTypeId = 6, level = "State", gender = "Male", ageGroup = "U-14", threshold = 4.8),
                Benchmark(eventTypeId = 6, level = "National", gender = "Male", ageGroup = "U-14", threshold = 5.5),
                Benchmark(eventTypeId = 6, level = "District", gender = "Male", ageGroup = "U-17", threshold = 4.5),
                Benchmark(eventTypeId = 6, level = "State", gender = "Male", ageGroup = "U-17", threshold = 5.2),
                Benchmark(eventTypeId = 6, level = "National", gender = "Male", ageGroup = "U-17", threshold = 6.0),
                // Long Jump - Female
                Benchmark(eventTypeId = 6, level = "District", gender = "Female", ageGroup = "U-17", threshold = 3.8),
                Benchmark(eventTypeId = 6, level = "State", gender = "Female", ageGroup = "U-17", threshold = 4.5),
                Benchmark(eventTypeId = 6, level = "National", gender = "Female", ageGroup = "U-17", threshold = 5.2),
                // High Jump - Male
                Benchmark(eventTypeId = 7, level = "District", gender = "Male", ageGroup = "U-17", threshold = 1.35),
                Benchmark(eventTypeId = 7, level = "State", gender = "Male", ageGroup = "U-17", threshold = 1.50),
                Benchmark(eventTypeId = 7, level = "National", gender = "Male", ageGroup = "U-17", threshold = 1.70),
                // Shot Put - Male
                Benchmark(eventTypeId = 8, level = "District", gender = "Male", ageGroup = "U-17", threshold = 10.0),
                Benchmark(eventTypeId = 8, level = "State", gender = "Male", ageGroup = "U-17", threshold = 12.0),
                Benchmark(eventTypeId = 8, level = "National", gender = "Male", ageGroup = "U-17", threshold = 14.0),
                // Shot Put - Female
                Benchmark(eventTypeId = 8, level = "District", gender = "Female", ageGroup = "U-17", threshold = 7.0),
                Benchmark(eventTypeId = 8, level = "State", gender = "Female", ageGroup = "U-17", threshold = 9.0),
                Benchmark(eventTypeId = 8, level = "National", gender = "Female", ageGroup = "U-17", threshold = 11.0)
            )
            benchmarkRepository.insertBenchmarks(benchmarks)
        }
    }
}
