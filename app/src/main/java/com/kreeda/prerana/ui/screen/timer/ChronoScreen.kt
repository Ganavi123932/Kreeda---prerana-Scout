package com.kreeda.prerana.ui.screen.timer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kreeda.prerana.ui.components.ChronoDisplay
import com.kreeda.prerana.ui.theme.*
import com.kreeda.prerana.ui.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronoScreen(
    navController: NavHostController,
    viewModel: ChronoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }

    // Save dialog
    if (showSaveDialog) {
        SaveToAthleteDialog(
            athletes = uiState.athletes,
            eventTypes = uiState.eventTypes,
            selectedAthleteId = uiState.selectedAthleteId,
            selectedEventTypeId = uiState.selectedEventTypeId,
            onAthleteSelect = viewModel::selectAthlete,
            onEventSelect = viewModel::selectEventType,
            onSave = {
                viewModel.saveToAthlete()
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⏱ Chronometer", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Timer display
            ChronoDisplay(
                elapsedMillis = uiState.elapsedMillis,
                isRunning = uiState.isRunning
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset
                if (uiState.elapsedMillis > 0 && !uiState.isRunning) {
                    FilledTonalButton(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                }

                // Start/Stop
                Button(
                    onClick = {
                        if (uiState.isRunning) viewModel.stop()
                        else viewModel.start()
                    },
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isRunning) Error else Primary
                    )
                ) {
                    Icon(
                        imageVector = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isRunning) "Stop" else "Start",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Lap
                if (uiState.isRunning) {
                    FilledTonalButton(
                        onClick = { viewModel.lap() },
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = "Lap")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button (when stopped with time)
            if (uiState.elapsedMillis > 0 && !uiState.isRunning) {
                Button(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save to Athlete", fontWeight = FontWeight.SemiBold)
                }
            }

            // Save confirmation
            uiState.saveMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Secondary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Secondary
                    )
                }
            }

            // Lap times
            if (uiState.laps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Lap Times",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    itemsIndexed(uiState.laps) { index, lapTime ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Lap ${index + 1}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = FormatUtils.formatChronoTime(lapTime),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaveToAthleteDialog(
    athletes: List<com.kreeda.prerana.data.db.entity.Athlete>,
    eventTypes: List<com.kreeda.prerana.data.db.entity.EventType>,
    selectedAthleteId: Long?,
    selectedEventTypeId: Long?,
    onAthleteSelect: (Long) -> Unit,
    onEventSelect: (Long) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var athleteExpanded by remember { mutableStateOf(false) }
    var eventExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Performance") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Athlete dropdown
                ExposedDropdownMenuBox(
                    expanded = athleteExpanded,
                    onExpandedChange = { athleteExpanded = it }
                ) {
                    OutlinedTextField(
                        value = athletes.find { it.id == selectedAthleteId }?.name ?: "Select Athlete",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Athlete") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = athleteExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = athleteExpanded,
                        onDismissRequest = { athleteExpanded = false }
                    ) {
                        athletes.forEach { athlete ->
                            DropdownMenuItem(
                                text = { Text(athlete.name) },
                                onClick = {
                                    onAthleteSelect(athlete.id)
                                    athleteExpanded = false
                                }
                            )
                        }
                    }
                }

                // Event dropdown
                ExposedDropdownMenuBox(
                    expanded = eventExpanded,
                    onExpandedChange = { eventExpanded = it }
                ) {
                    OutlinedTextField(
                        value = eventTypes.find { it.id == selectedEventTypeId }?.name ?: "Select Event",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Event") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = eventExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = eventExpanded,
                        onDismissRequest = { eventExpanded = false }
                    ) {
                        eventTypes.forEach { event ->
                            DropdownMenuItem(
                                text = { Text(event.name) },
                                onClick = {
                                    onEventSelect(event.id)
                                    eventExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = selectedAthleteId != null && selectedEventTypeId != null
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
