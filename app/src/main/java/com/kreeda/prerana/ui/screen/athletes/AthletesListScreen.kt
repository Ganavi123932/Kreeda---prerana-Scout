package com.kreeda.prerana.ui.screen.athletes

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kreeda.prerana.ui.components.AthleteCard
import com.kreeda.prerana.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthletesListScreen(
    navController: NavHostController,
    viewModel: AthletesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val badgeCounts by viewModel.badgeCounts.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Athletes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditAthlete.createRoute()) },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Add Athlete") },
                text = { Text("Add Athlete") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search athletes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            // Filter chips (animated)
            AnimatedVisibility(visible = showFilters) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Gender filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.selectedGender == null,
                            onClick = { viewModel.onGenderFilterChange(null) },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = uiState.selectedGender == "Male",
                            onClick = { viewModel.onGenderFilterChange("Male") },
                            label = { Text("Male") }
                        )
                        FilterChip(
                            selected = uiState.selectedGender == "Female",
                            onClick = { viewModel.onGenderFilterChange("Female") },
                            label = { Text("Female") }
                        )
                    }
                    // Sport filter
                    if (uiState.availableSports.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = uiState.selectedSport == null,
                                onClick = { viewModel.onSportFilterChange(null) },
                                label = { Text("All Sports") }
                            )
                            uiState.availableSports.take(4).forEach { sport ->
                                FilterChip(
                                    selected = uiState.selectedSport == sport,
                                    onClick = { viewModel.onSportFilterChange(sport) },
                                    label = { Text(sport) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Athletes list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.athletes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏃", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "No athletes found"
                            else "No athletes yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap + to add your first athlete",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.athletes,
                        key = { it.id }
                    ) { athlete ->
                        AthleteCard(
                            athlete = athlete,
                            badgeCount = badgeCounts[athlete.id] ?: 0,
                            onClick = {
                                navController.navigate(
                                    Screen.AthleteProfile.createRoute(athlete.id)
                                )
                            }
                        )
                    }
                    // Bottom spacer for FAB
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}
