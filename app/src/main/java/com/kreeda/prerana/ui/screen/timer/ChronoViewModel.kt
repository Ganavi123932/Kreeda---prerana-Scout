package com.kreeda.prerana.ui.screen.timer

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.data.db.entity.EventType
import com.kreeda.prerana.data.db.entity.Performance
import com.kreeda.prerana.data.repository.BenchmarkRepository
import com.kreeda.prerana.domain.usecase.EvaluateBadgeUseCase
import com.kreeda.prerana.domain.usecase.LogPerformanceUseCase
import com.kreeda.prerana.domain.usecase.ManageAthleteUseCase
import com.kreeda.prerana.ui.util.FormatUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChronoUiState(
    val elapsedMillis: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<Long> = emptyList(), // List of lap times in millis
    val athletes: List<Athlete> = emptyList(),
    val eventTypes: List<EventType> = emptyList(),
    val selectedAthleteId: Long? = null,
    val selectedEventTypeId: Long? = null,
    val isSaved: Boolean = false,
    val saveMessage: String? = null
)

@HiltViewModel
class ChronoViewModel @Inject constructor(
    private val manageAthleteUseCase: ManageAthleteUseCase,
    private val logPerformanceUseCase: LogPerformanceUseCase,
    private val evaluateBadgeUseCase: EvaluateBadgeUseCase,
    private val benchmarkRepository: BenchmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChronoUiState())
    val uiState: StateFlow<ChronoUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0L
    private var accumulatedTime: Long = 0L

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            manageAthleteUseCase.getAllAthletes().collect { athletes ->
                _uiState.update { it.copy(athletes = athletes) }
            }
        }
        viewModelScope.launch {
            benchmarkRepository.getAllEventTypes().collect { types ->
                _uiState.update { it.copy(eventTypes = types.filter { t -> t.unit == "seconds" }) }
            }
        }
    }

    fun start() {
        if (_uiState.value.isRunning) return
        startTime = SystemClock.elapsedRealtime()
        _uiState.update { it.copy(isRunning = true, isSaved = false, saveMessage = null) }

        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = accumulatedTime + (SystemClock.elapsedRealtime() - startTime)
                _uiState.update { it.copy(elapsedMillis = elapsed) }
                delay(10) // Update every 10ms for smooth display
            }
        }
    }

    fun stop() {
        if (!_uiState.value.isRunning) return
        accumulatedTime += SystemClock.elapsedRealtime() - startTime
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false, elapsedMillis = accumulatedTime) }
    }

    fun lap() {
        if (!_uiState.value.isRunning) return
        val currentElapsed = accumulatedTime + (SystemClock.elapsedRealtime() - startTime)
        _uiState.update { it.copy(laps = it.laps + currentElapsed) }
    }

    fun reset() {
        timerJob?.cancel()
        accumulatedTime = 0L
        _uiState.update {
            it.copy(
                elapsedMillis = 0L,
                isRunning = false,
                laps = emptyList(),
                isSaved = false,
                saveMessage = null
            )
        }
    }

    fun selectAthlete(athleteId: Long) {
        _uiState.update { it.copy(selectedAthleteId = athleteId) }
    }

    fun selectEventType(eventTypeId: Long) {
        _uiState.update { it.copy(selectedEventTypeId = eventTypeId) }
    }

    fun saveToAthlete() {
        val state = _uiState.value
        val athleteId = state.selectedAthleteId ?: return
        val eventTypeId = state.selectedEventTypeId ?: return
        val elapsed = state.elapsedMillis
        if (elapsed <= 0) return

        val seconds = FormatUtils.chronoMillisToSeconds(elapsed)

        viewModelScope.launch {
            logPerformanceUseCase.logPerformance(
                athleteId = athleteId,
                eventTypeId = eventTypeId,
                value = seconds
            )

            // Evaluate badges
            val athlete = manageAthleteUseCase.getAthlete(athleteId).first()
            if (athlete != null) {
                val ageGroup = FormatUtils.getAgeGroup(athlete.age)
                val newBadges = evaluateBadgeUseCase.evaluateBadges(
                    athleteId = athleteId,
                    eventTypeId = eventTypeId,
                    gender = athlete.gender,
                    ageGroup = ageGroup
                )

                val message = if (newBadges.isNotEmpty()) {
                    "Saved! 🎉 New badge${if (newBadges.size > 1) "s" else ""}: ${
                        newBadges.joinToString { "${FormatUtils.getBadgeEmoji(it.level)} ${it.level}" }
                    }"
                } else {
                    "Performance saved successfully! ✅"
                }
                _uiState.update { it.copy(isSaved = true, saveMessage = message) }
            }
        }
    }
}
