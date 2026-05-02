package com.kreeda.prerana.ui.screen.performance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kreeda.prerana.ui.theme.Error
import com.kreeda.prerana.ui.theme.Secondary
import com.kreeda.prerana.ui.util.DateUtils
import com.kreeda.prerana.ui.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceHistoryScreen(
    athleteId: Long,
    navController: NavHostController,
    viewModel: PerformanceHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(athleteId) {
        viewModel.loadAthlete(athleteId)
    }

    // Add Performance Dialog
    if (uiState.showAddDialog) {
        var eventExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            title = { Text("Log Performance") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Event type dropdown
                    ExposedDropdownMenuBox(
                        expanded = eventExpanded,
                        onExpandedChange = { eventExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.eventTypes.find { it.id == uiState.addEventTypeId }?.name ?: "Select Event",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Event Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = eventExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = eventExpanded,
                            onDismissRequest = { eventExpanded = false }
                        ) {
                            uiState.eventTypes.forEach { event ->
                                DropdownMenuItem(
                                    text = { Text("${event.name} (${event.unit})") },
                                    onClick = {
                                        viewModel.onAddEventTypeChange(event.id)
                                        eventExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Value input
                    OutlinedTextField(
                        value = uiState.addValue,
                        onValueChange = viewModel::onAddValueChange,
                        label = {
                            val unit = uiState.eventTypes.find { it.id == uiState.addEventTypeId }?.unit ?: "value"
                            Text("Value ($unit)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Notes
                    OutlinedTextField(
                        value = uiState.addNotes,
                        onValueChange = viewModel::onAddNotesChange,
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.savePerformance() },
                    enabled = uiState.addEventTypeId != null && uiState.addValue.toDoubleOrNull() != null
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideAddDialog() }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Performance History", fontWeight = FontWeight.Bold)
                        uiState.athlete?.let {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Performance") },
                text = { Text("Log Performance") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Save confirmation message
            uiState.saveMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else if (uiState.performances.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No performances recorded",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap + to log the first performance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = uiState.performances,
                        key = { it.id }
                    ) { perf ->
                        var showDelete by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = viewModel.getEventTypeName(perf.eventTypeId),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = DateUtils.formatDateTime(perf.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    perf.notes?.let { notes ->
                                        Text(
                                            text = notes,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = FormatUtils.formatPerformanceValue(
                                        perf.value,
                                        viewModel.getEventTypeUnit(perf.eventTypeId)
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = { viewModel.deletePerformance(perf) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}
