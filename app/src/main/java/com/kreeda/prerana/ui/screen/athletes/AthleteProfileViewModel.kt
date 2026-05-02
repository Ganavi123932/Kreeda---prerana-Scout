package com.kreeda.prerana.ui.screen.athletes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.db.entity.Badge
import com.kreeda.prerana.data.db.entity.EventType
import com.kreeda.prerana.data.db.entity.Performance
import com.kreeda.prerana.data.repository.BadgeRepository
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.data.repository.PerformanceRepository
import com.kreeda.prerana.domain.usecase.ManageAthleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AthleteProfileUiState(
    val athlete: Athlete? = null,
    val badges: List<Badge> = emptyList(),
    val recentPerformances: List<Performance> = emptyList(),
    val eventTypes: List<EventType> = emptyList(),
    val performedEventTypeIds: List<Long> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AthleteProfileViewModel @Inject constructor(
    private val manageAthleteUseCase: ManageAthleteUseCase,
    private val badgeRepository: BadgeRepository,
    private val performanceRepository: PerformanceRepository,
    private val benchmarkRepository: BenchmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AthleteProfileUiState())
    val uiState: StateFlow<AthleteProfileUiState> = _uiState.asStateFlow()

    fun loadAthlete(athleteId: Long) {
        viewModelScope.launch {
            manageAthleteUseCase.getAthlete(athleteId).collect { athlete ->
                _uiState.update { it.copy(athlete = athlete, isLoading = false) }
            }
        }
        viewModelScope.launch {
            badgeRepository.getBadgesForAthlete(athleteId).collect { badges ->
                _uiState.update { it.copy(badges = badges) }
            }
        }
        viewModelScope.launch {
            performanceRepository.getPerformancesForAthlete(athleteId).collect { performances ->
                _uiState.update { it.copy(recentPerformances = performances.take(10)) }
            }
        }
        viewModelScope.launch {
            benchmarkRepository.getAllEventTypes().collect { types ->
                _uiState.update { it.copy(eventTypes = types) }
            }
        }
        viewModelScope.launch {
            val eventIds = performanceRepository.getEventTypesForAthlete(athleteId)
            _uiState.update { it.copy(performedEventTypeIds = eventIds) }
        }
    }

    fun deleteAthlete(onComplete: () -> Unit) {
        val athlete = _uiState.value.athlete ?: return
        viewModelScope.launch {
            manageAthleteUseCase.deleteAthlete(athlete)
            onComplete()
        }
    }

    fun getEventTypeName(eventTypeId: Long): String {
        return _uiState.value.eventTypes.find { it.id == eventTypeId }?.name ?: "Unknown Event"
    }

    fun getEventTypeUnit(eventTypeId: Long): String {
        return _uiState.value.eventTypes.find { it.id == eventTypeId }?.unit ?: ""
    }
}
