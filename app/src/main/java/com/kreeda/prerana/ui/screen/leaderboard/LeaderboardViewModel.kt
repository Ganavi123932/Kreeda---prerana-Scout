package com.kreeda.prerana.ui.screen.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.data.db.entity.EventType
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.domain.model.LeaderboardEntry
import com.kreeda.prerana.domain.usecase.GetLeaderboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderboardUiState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val eventTypes: List<EventType> = emptyList(),
    val selectedEventTypeId: Long? = null,
    val selectedGender: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getLeaderboardUseCase: GetLeaderboardUseCase,
    private val benchmarkRepository: BenchmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadEventTypes()
    }

    private fun loadEventTypes() {
        viewModelScope.launch {
            benchmarkRepository.getAllEventTypes().collect { types ->
                _uiState.update { it.copy(eventTypes = types) }
                // Auto-select first event type
                if (types.isNotEmpty() && _uiState.value.selectedEventTypeId == null) {
                    selectEventType(types.first().id)
                }
            }
        }
    }

    fun selectEventType(eventTypeId: Long) {
        _uiState.update { it.copy(selectedEventTypeId = eventTypeId, isLoading = true) }
        loadLeaderboard()
    }

    fun selectGender(gender: String?) {
        _uiState.update { it.copy(selectedGender = gender, isLoading = true) }
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        val eventTypeId = _uiState.value.selectedEventTypeId ?: return
        val gender = _uiState.value.selectedGender

        viewModelScope.launch {
            getLeaderboardUseCase.getLeaderboard(
                eventTypeId = eventTypeId,
                gender = gender
            ).collect { entries ->
                _uiState.update { it.copy(entries = entries, isLoading = false) }
            }
        }
    }
}
