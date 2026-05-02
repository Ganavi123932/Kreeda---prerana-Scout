package com.kreeda.prerana.ui.screen.leaderboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kreeda.prerana.ui.components.LeaderboardRow
import com.kreeda.prerana.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavHostController,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏆 Leaderboard", fontWeight = FontWeight.Bold) },
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
        ) {
            // Event type filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.eventTypes.forEach { eventType ->
                    FilterChip(
                        selected = uiState.selectedEventTypeId == eventType.id,
                        onClick = { viewModel.selectEventType(eventType.id) },
                        label = { Text(eventType.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            // Gender filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedGender == null,
                    onClick = { viewModel.selectGender(null) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.selectedGender == "Male",
                    onClick = { viewModel.selectGender("Male") },
                    label = { Text("Male") }
                )
                FilterChip(
                    selected = uiState.selectedGender == "Female",
                    onClick = { viewModel.selectGender("Female") },
                    label = { Text("Female") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Leaderboard content
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else if (uiState.entries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏆", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No rankings yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Log performances to build the leaderboard",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = uiState.entries,
                        key = { "${it.rank}-${it.athlete.id}" }
                    ) { entry ->
                        LeaderboardRow(
                            rank = entry.rank,
                            athleteName = entry.athlete.name,
                            value = entry.bestValue,
                            unit = entry.unit,
                            badgeLevel = entry.badgeLevel
                        )
                    }
                }
            }
        }
    }
}
