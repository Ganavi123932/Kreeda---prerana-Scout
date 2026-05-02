package com.kreeda.prerana.ui.screen.athletes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.repository.BadgeRepository
import com.kreeda.prerana.domain.usecase.ManageAthleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AthletesListUiState(
    val athletes: List<Athlete> = emptyList(),
    val searchQuery: String = "",
    val selectedSport: String? = null,
    val selectedGender: String? = null,
    val availableSports: List<String> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AthletesListViewModel @Inject constructor(
    private val manageAthleteUseCase: ManageAthleteUseCase,
    private val badgeRepository: BadgeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AthletesListUiState())
    val uiState: StateFlow<AthletesListUiState> = _uiState.asStateFlow()

    private val _badgeCounts = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val badgeCounts: StateFlow<Map<Long, Int>> = _badgeCounts.asStateFlow()

    init {
        loadAthletes()
        loadSports()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadAthletes() {
        viewModelScope.launch {
            combine(
                _uiState.map { it.searchQuery }.distinctUntilChanged(),
                _uiState.map { it.selectedSport }.distinctUntilChanged(),
                _uiState.map { it.selectedGender }.distinctUntilChanged()
            ) { query: String, sport: String?, gender: String? ->
                Triple(query, sport, gender)
            }.flatMapLatest { (query, sport, gender) ->
                if (query.isNotBlank()) {
                    manageAthleteUseCase.searchAthletes(query)
                } else {
                    manageAthleteUseCase.filterAthletes(sport = sport, gender = gender)
                }
            }.collect { athletes ->
                _uiState.update { it.copy(athletes = athletes, isLoading = false) }
                // Load badge counts for each athlete
                val counts = mutableMapOf<Long, Int>()
                for (athlete in athletes) {
                    badgeRepository.getBadgeCountForAthlete(athlete.id).firstOrNull()?.let { count ->
                        counts[athlete.id] = count
                    }
                }
                _badgeCounts.value = counts
            }
        }
    }

    private fun loadSports() {
        viewModelScope.launch {
            manageAthleteUseCase.getAllSports().collect { sports ->
                _uiState.update { it.copy(availableSports = sports) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSportFilterChange(sport: String?) {
        _uiState.update { it.copy(selectedSport = sport) }
    }

    fun onGenderFilterChange(gender: String?) {
        _uiState.update { it.copy(selectedGender = gender) }
    }

    fun deleteAthlete(athlete: Athlete) {
        viewModelScope.launch {
            manageAthleteUseCase.deleteAthlete(athlete)
        }
    }
}
