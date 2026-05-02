package com.kreeda.prerana.ui.screen.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.db.entity.EventType
import com.kreeda.prerana.data.db.entity.Performance
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.data.repository.PerformanceRepository
import com.kreeda.prerana.domain.usecase.EvaluateBadgeUseCase
import com.kreeda.prerana.domain.usecase.LogPerformanceUseCase
import com.kreeda.prerana.domain.usecase.ManageAthleteUseCase
import com.kreeda.prerana.ui.util.FormatUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerformanceHistoryUiState(
    val athlete: Athlete? = null,
    val performances: List<Performance> = emptyList(),
    val eventTypes: List<EventType> = emptyList(),
    val selectedEventTypeId: Long? = null,
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val addEventTypeId: Long? = null,
    val addValue: String = "",
    val addNotes: String = "",
    val saveMessage: String? = null
)

@HiltViewModel
class PerformanceHistoryViewModel @Inject constructor(
    private val manageAthleteUseCase: ManageAthleteUseCase,
    private val performanceRepository: PerformanceRepository,
    private val logPerformanceUseCase: LogPerformanceUseCase,
    private val evaluateBadgeUseCase: EvaluateBadgeUseCase,
    private val benchmarkRepository: BenchmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerformanceHistoryUiState())
    val uiState: StateFlow<PerformanceHistoryUiState> = _uiState.asStateFlow()

    fun loadAthlete(athleteId: Long) {
        viewModelScope.launch {
            manageAthleteUseCase.getAthlete(athleteId).collect { athlete ->
                _uiState.update { it.copy(athlete = athlete, isLoading = false) }
            }
        }
        viewModelScope.launch {
            performanceRepository.getPerformancesForAthlete(athleteId).collect { performances ->
                _uiState.update { it.copy(performances = performances) }
            }
        }
        viewModelScope.launch {
            benchmarkRepository.getAllEventTypes().collect { types ->
                _uiState.update { it.copy(eventTypes = types) }
            }
        }
    }

    fun getEventTypeName(eventTypeId: Long): String {
        return _uiState.value.eventTypes.find { it.id == eventTypeId }?.name ?: "Unknown"
    }

    fun getEventTypeUnit(eventTypeId: Long): String {
        return _uiState.value.eventTypes.find { it.id == eventTypeId }?.unit ?: ""
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, addValue = "", addNotes = "", saveMessage = null) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun onAddEventTypeChange(eventTypeId: Long) {
        _uiState.update { it.copy(addEventTypeId = eventTypeId) }
    }

    fun onAddValueChange(value: String) {
        _uiState.update { it.copy(addValue = value) }
    }

    fun onAddNotesChange(notes: String) {
        _uiState.update { it.copy(addNotes = notes) }
    }

    fun savePerformance() {
        val state = _uiState.value
        val athlete = state.athlete ?: return
        val eventTypeId = state.addEventTypeId ?: return
        val value = state.addValue.toDoubleOrNull() ?: return

        viewModelScope.launch {
            logPerformanceUseCase.logPerformance(
                athleteId = athlete.id,
                eventTypeId = eventTypeId,
                value = value,
                notes = state.addNotes.ifBlank { null }
            )

            // Evaluate badges
            val ageGroup = FormatUtils.getAgeGroup(athlete.age)
            val newBadges = evaluateBadgeUseCase.evaluateBadges(
                athleteId = athlete.id,
                eventTypeId = eventTypeId,
                gender = athlete.gender,
                ageGroup = ageGroup
            )

            val message = if (newBadges.isNotEmpty()) {
                "🎉 New badge: ${newBadges.joinToString { FormatUtils.getBadgeLabel(it.level) }}"
            } else {
                "Performance saved ✅"
            }

            _uiState.update { it.copy(showAddDialog = false, saveMessage = message) }
        }
    }

    fun deletePerformance(performance: Performance) {
        viewModelScope.launch {
            performanceRepository.deletePerformance(performance)
        }
    }
}
