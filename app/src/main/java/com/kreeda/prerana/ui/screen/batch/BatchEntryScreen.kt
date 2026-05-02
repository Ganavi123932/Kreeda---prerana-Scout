package com.kreeda.prerana.ui.screen.batch

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kreeda.prerana.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchEntryScreen(
    navController: NavHostController,
    viewModel: BatchEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("📝 Batch Entry", fontWeight = FontWeight.Bold)
                        if (uiState.batchItems.isNotEmpty()) {
                            Text(
                                text = "${uiState.batchItems.size} athletes",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Event type selector
            var eventExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = eventExpanded,
                onExpandedChange = { eventExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.eventTypes.find { it.id == uiState.selectedEventTypeId }?.name
                        ?: "Select Event Type",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Event Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = eventExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                ExposedDropdownMenu(
                    expanded = eventExpanded,
                    onDismissRequest = { eventExpanded = false }
                ) {
                    uiState.eventTypes.forEach { event ->
                        DropdownMenuItem(
                            text = { Text("${event.name} (${event.unit})") },
                            onClick = {
                                viewModel.selectEventType(event.id)
                                eventExpanded = false
                            }
                        )
                    }
                }
            }

            // Completion summary
            if (uiState.isComplete) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Secondary.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "✅ Batch Entry Complete!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.savedCount} performances saved",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (uiState.totalBadgesAwarded > 0) {
                            Text(
                                text = "🎉 ${uiState.totalBadgesAwarded} new badges awarded!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { viewModel.resetBatch() }) {
                            Text("Start New Batch")
                        }
                    }
                }
            }

            // Athletes list with input fields
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else if (uiState.batchItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👥", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No athletes registered",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Add athletes first to use batch entry",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(
                        items = uiState.batchItems,
                        key = { _, item -> item.athlete.id }
                    ) { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isSaved)
                                    Secondary.copy(alpha = 0.05f)
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Index number
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.width(28.dp)
                                )

                                // Athlete name
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.athlete.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (item.badgeAwarded != null) {
                                        Text(
                                            text = "🎉 ${item.badgeAwarded}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Primary
                                        )
                                    }
                                }

                                // Value input
                                OutlinedTextField(
                                    value = item.value,
                                    onValueChange = { viewModel.updateValue(index, it) },
                                    modifier = Modifier.width(120.dp),
                                    enabled = !item.isSaved,
                                    singleLine = true,
                                    placeholder = { Text("Value") },
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )

                                // Saved indicator
                                if (item.isSaved) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Saved",
                                        tint = Secondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Save all button
                if (!uiState.isComplete && uiState.selectedEventTypeId != null) {
                    Button(
                        onClick = { viewModel.saveAllEntries() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .height(56.dp),
                        enabled = !uiState.isSaving && uiState.batchItems.any { it.value.isNotBlank() },
                        shape = MaterialTheme.shapes.large
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saving...")
                        } else {
                            Icon(Icons.Default.SaveAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save All Entries",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
