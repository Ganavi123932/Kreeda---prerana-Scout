package com.kreeda.prerana.ui.screen.performance

import androidx.compose.foundation.layout.*
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
import com.kreeda.prerana.ui.components.TalentCurveChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentCurveScreen(
    athleteId: Long,
    eventTypeId: Long,
    navController: NavHostController,
    viewModel: TalentCurveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(athleteId, eventTypeId) {
        viewModel.loadCurve(athleteId, eventTypeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📈 Talent Curve", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            uiState.curveData != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Athlete info
                    val data = uiState.curveData!!
                    Text(
                        text = data.athleteName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${data.eventName} • ${data.dataPoints.size} records",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Chart
                    TalentCurveChart(
                        data = data,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Benchmark info cards
                    Text(
                        text = "Benchmark Thresholds",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        data.districtBenchmark?.let { threshold ->
                            BenchmarkCard(
                                modifier = Modifier.weight(1f),
                                level = "District",
                                value = threshold,
                                unit = data.unit
                            )
                        }
                        data.stateBenchmark?.let { threshold ->
                            BenchmarkCard(
                                modifier = Modifier.weight(1f),
                                level = "State",
                                value = threshold,
                                unit = data.unit
                            )
                        }
                        data.nationalBenchmark?.let { threshold ->
                            BenchmarkCard(
                                modifier = Modifier.weight(1f),
                                level = "National",
                                value = threshold,
                                unit = data.unit
                            )
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No performance data available for this event.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BenchmarkCard(
    modifier: Modifier = Modifier,
    level: String,
    value: Double,
    unit: String
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = com.kreeda.prerana.ui.util.FormatUtils.getBadgeEmoji(level),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = level,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = com.kreeda.prerana.ui.util.FormatUtils.formatPerformanceValue(value, unit),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
