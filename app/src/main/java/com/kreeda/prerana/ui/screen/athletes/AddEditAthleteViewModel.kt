package com.kreeda.prerana.ui.screen.athletes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreeda.prerana.data.db.entity.Athlete
import com.kreeda.prerana.domain.usecase.ManageAthleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditAthleteUiState(
    val name: String = "",
    val age: String = "",
    val gender: String = "Male",
    val school: String = "",
    val studentClass: String = "",
    val primarySport: String = "Athletics",
    val nameError: String? = null,
    val ageError: String? = null,
    val schoolError: String? = null,
    val isSaving: Boolean = false,
    val editingAthleteId: Long? = null
)

@HiltViewModel
class AddEditAthleteViewModel @Inject constructor(
    private val manageAthleteUseCase: ManageAthleteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditAthleteUiState())
    val uiState: StateFlow<AddEditAthleteUiState> = _uiState.asStateFlow()

    fun loadAthlete(athleteId: Long) {
        viewModelScope.launch {
            manageAthleteUseCase.getAthlete(athleteId).first()?.let { athlete ->
                _uiState.update {
                    it.copy(
                        name = athlete.name,
                        age = athlete.age.toString(),
                        gender = athlete.gender,
                        school = athlete.school,
                        studentClass = athlete.studentClass,
                        primarySport = athlete.primarySport,
                        editingAthleteId = athlete.id
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onAgeChange(age: String) {
        // Only allow digits
        if (age.all { it.isDigit() } || age.isEmpty()) {
            _uiState.update { it.copy(age = age, ageError = null) }
        }
    }

    fun onGenderChange(gender: String) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onSchoolChange(school: String) {
        _uiState.update { it.copy(school = school, schoolError = null) }
    }

    fun onClassChange(studentClass: String) {
        _uiState.update { it.copy(studentClass = studentClass) }
    }

    fun onSportChange(sport: String) {
        _uiState.update { it.copy(primarySport = sport) }
    }

    fun saveAthlete(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validation
        var hasError = false
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Name is required") }
            hasError = true
        }
        if (state.age.isBlank() || state.age.toIntOrNull() == null) {
            _uiState.update { it.copy(ageError = "Valid age is required") }
            hasError = true
        } else {
            val ageInt = state.age.toInt()
            if (ageInt < 5 || ageInt > 25) {
                _uiState.update { it.copy(ageError = "Age must be between 5 and 25") }
                hasError = true
            }
        }
        if (state.school.isBlank()) {
            _uiState.update { it.copy(schoolError = "School name is required") }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                if (state.editingAthleteId != null) {
                    // Update existing athlete
                    val athlete = Athlete(
                        id = state.editingAthleteId,
                        name = state.name.trim(),
                        age = state.age.toInt(),
                        gender = state.gender,
                        school = state.school.trim(),
                        studentClass = state.studentClass.trim(),
                        primarySport = state.primarySport
                    )
                    manageAthleteUseCase.updateAthlete(athlete)
                } else {
                    // Create new athlete
                    manageAthleteUseCase.createAthlete(
                        name = state.name.trim(),
                        age = state.age.toInt(),
                        gender = state.gender,
                        school = state.school.trim(),
                        studentClass = state.studentClass.trim(),
                        primarySport = state.primarySport
                    )
                }
                onSuccess()
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
