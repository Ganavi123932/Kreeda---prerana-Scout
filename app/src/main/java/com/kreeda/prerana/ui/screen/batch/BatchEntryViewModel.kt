package com.kreeda.prerana.ui.screen.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.db.entity.EventType
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.domain.usecase.EvaluateBadgeUseCase
import com.kreeda.prerana.domain.usecase.LogPerformanceUseCase
import com.kreeda.prerana.domain.usecase.ManageAthleteUseCase
import com.kreeda.prerana.ui.util.FormatUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BatchEntryItem(
    val athlete: Athlete,
    val value: String = "",
    val isSaved: Boolean = false,
    val badgeAwarded: String? = null
)

data class BatchEntryUiState(
    val athletes: List<Athlete> = emptyList(),
    val eventTypes: List<EventType> = emptyList(),
    val selectedEventTypeId: Long? = null,
    val batchItems: List<BatchEntryItem> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val savedCount: Int = 0,
    val totalBadgesAwarded: Int = 0,
    val isComplete: Boolean = false
)

@HiltViewModel
class BatchEntryViewModel @Inject constructor(
    private val manageAthleteUseCase: ManageAthleteUseCase,
    private val logPerformanceUseCase: LogPerformanceUseCase,
    private val evaluateBadgeUseCase: EvaluateBadgeUseCase,
    private val benchmarkRepository: BenchmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BatchEntryUiState())
    val uiState: StateFlow<BatchEntryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            manageAthleteUseCase.getAllAthletes().collect { athletes ->
                _uiState.update {
                    it.copy(
                        athletes = athletes,
                        batchItems = athletes.map { a -> BatchEntryItem(athlete = a) },
                        isLoading = false
                    )
                }
            }
        }
        viewModelScope.launch {
            benchmarkRepository.getAllEventTypes().collect { types ->
                _uiState.update { it.copy(eventTypes = types) }
            }
        }
    }

    fun selectEventType(eventTypeId: Long) {
        _uiState.update { it.copy(selectedEventTypeId = eventTypeId) }
    }

    fun updateValue(index: Int, value: String) {
        val items = _uiState.value.batchItems.toMutableList()
        if (index in items.indices) {
            items[index] = items[index].copy(value = value)
            _uiState.update { it.copy(batchItems = items) }
        }
    }

    fun moveToNext() {
        val state = _uiState.value
        if (state.currentIndex < state.batchItems.size - 1) {
            _uiState.update { it.copy(currentIndex = it.currentIndex + 1) }
        }
    }

    fun moveToPrevious() {
        if (_uiState.value.currentIndex > 0) {
            _uiState.update { it.copy(currentIndex = it.currentIndex - 1) }
        }
    }

    fun saveAllEntries() {
        val state = _uiState.value
        val eventTypeId = state.selectedEventTypeId ?: return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            var savedCount = 0
            var badgesAwarded = 0
            val updatedItems = state.batchItems.toMutableList()

            for (i in updatedItems.indices) {
                val item = updatedItems[i]
                val value = item.value.toDoubleOrNull() ?: continue

                logPerformanceUseCase.logPerformance(
                    athleteId = item.athlete.id,
                    eventTypeId = eventTypeId,
                    value = value
                )
                savedCount++

                // Evaluate badges
                val ageGroup = FormatUtils.getAgeGroup(item.athlete.age)
                val newBadges = evaluateBadgeUseCase.evaluateBadges(
                    athleteId = item.athlete.id,
                    eventTypeId = eventTypeId,
                    gender = item.athlete.gender,
                    ageGroup = ageGroup
                )

                val badgeLabel = newBadges.firstOrNull()?.let { FormatUtils.getBadgeLabel(it.level) }
                badgesAwarded += newBadges.size

                updatedItems[i] = item.copy(isSaved = true, badgeAwarded = badgeLabel)
            }

            _uiState.update {
                it.copy(
                    batchItems = updatedItems,
                    isSaving = false,
                    savedCount = savedCount,
                    totalBadgesAwarded = badgesAwarded,
                    isComplete = true
                )
            }
        }
    }

    fun resetBatch() {
        _uiState.update {
            it.copy(
                batchItems = it.athletes.map { a -> BatchEntryItem(athlete = a) },
                currentIndex = 0,
                savedCount = 0,
                totalBadgesAwarded = 0,
                isComplete = false
            )
        }
    }
}
