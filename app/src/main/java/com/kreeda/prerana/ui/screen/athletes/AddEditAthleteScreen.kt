package com.kreeda.prerana.ui.screen.athletes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAthleteScreen(
    athleteId: Long?,
    navController: NavHostController,
    viewModel: AddEditAthleteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditing = athleteId != null

    LaunchedEffect(athleteId) {
        if (athleteId != null) {
            viewModel.loadAthlete(athleteId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Athlete" else "Add Athlete",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Full Name *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.nameError != null,
                supportingText = uiState.nameError?.let { { Text(it) } }
            )

            // Age
            OutlinedTextField(
                value = uiState.age,
                onValueChange = viewModel::onAgeChange,
                label = { Text("Age *") },
                leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.ageError != null,
                supportingText = uiState.ageError?.let { { Text(it) } }
            )

            // Gender
            Text(
                text = "Gender *",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Male", "Female", "Other").forEach { gender ->
                    FilterChip(
                        selected = uiState.gender == gender,
                        onClick = { viewModel.onGenderChange(gender) },
                        label = { Text(gender) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // School
            OutlinedTextField(
                value = uiState.school,
                onValueChange = viewModel::onSchoolChange,
                label = { Text("School Name *") },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.schoolError != null,
                supportingText = uiState.schoolError?.let { { Text(it) } }
            )

            // Class
            OutlinedTextField(
                value = uiState.studentClass,
                onValueChange = viewModel::onClassChange,
                label = { Text("Class *") },
                leadingIcon = { Icon(Icons.Default.Class, contentDescription = null) },
                placeholder = { Text("e.g., 8th, 9th, 10th") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Primary Sport
            Text(
                text = "Primary Sport *",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val sports = listOf("Athletics", "Kabaddi", "Kho-Kho", "Cricket", "Football", "Hockey", "Badminton", "Volleyball", "Basketball", "Other")

            Column {
                sports.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { sport ->
                            FilterChip(
                                selected = uiState.primarySport == sport,
                                onClick = { viewModel.onSportChange(sport) },
                                label = { Text(sport, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining space if row is not complete
                        repeat(3 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save button
            Button(
                onClick = {
                    viewModel.saveAthlete {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSaving,
                shape = MaterialTheme.shapes.large
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        if (isEditing) Icons.Default.Save else Icons.Default.PersonAdd,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditing) "Update Athlete" else "Add Athlete",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
