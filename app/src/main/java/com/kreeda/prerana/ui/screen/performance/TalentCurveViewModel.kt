package com.kreeda.prerana.ui.screen.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.domain.model.TalentCurveData
import com.kreeda.prerana.domain.usecase.GenerateTalentCurveUseCase
import com.kreeda.prerana.domain.usecase.ManageAthleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.kreeda.prerana.ui.util.FormatUtils
import javax.inject.Inject

data class TalentCurveUiState(
    val curveData: TalentCurveData? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TalentCurveViewModel @Inject constructor(
    private val generateTalentCurveUseCase: GenerateTalentCurveUseCase,
    private val manageAthleteUseCase: ManageAthleteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TalentCurveUiState())
    val uiState: StateFlow<TalentCurveUiState> = _uiState.asStateFlow()

    fun loadCurve(athleteId: Long, eventTypeId: Long) {
        viewModelScope.launch {
            try {
                val athlete = manageAthleteUseCase.getAthlete(athleteId).first()
                if (athlete == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Athlete not found") }
                    return@launch
                }

                val ageGroup = FormatUtils.getAgeGroup(athlete.age)
                val curveData = generateTalentCurveUseCase.generateTalentCurve(
                    athleteId = athleteId,
                    athleteName = athlete.name,
                    eventTypeId = eventTypeId,
                    gender = athlete.gender,
                    ageGroup = ageGroup
                )

                _uiState.update { it.copy(curveData = curveData, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
