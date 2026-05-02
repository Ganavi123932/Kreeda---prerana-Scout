package com.kreeda.prerana.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreeda.prerana.domain.model.TalentCurveData
import com.kreeda.prerana.ui.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

/**
 * Talent curve chart showing performance progression over time
 * with benchmark overlay lines for District/State/National thresholds.
 */
@Composable
fun TalentCurveChart(
    data: TalentCurveData,
    modifier: Modifier = Modifier
) {
    if (data.dataPoints.isEmpty()) {
        Text(
            text = "No performance data available yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val entries = data.dataPoints.mapIndexed { index, point ->
        FloatEntry(index.toFloat(), point.value.toFloat())
    }

    val chartEntryModelProducer = remember(entries) {
        ChartEntryModelProducer(listOf(entries))
    }

    Column(modifier = modifier) {
        // Chart title
        Text(
            text = "${data.eventName} — Talent Curve",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Chart(
            chart = lineChart(
                lines = listOf(
                    lineSpec(
                        lineColor = ChartLine
                    )
                ),
                decorations = buildList {
                    data.districtBenchmark?.let { threshold ->
                        add(
                            ThresholdLine(
                                thresholdValue = threshold.toFloat(),
                                lineComponent = shapeComponent(
                                    shape = Shapes.rectShape,
                                    color = ChartDistrictLine,
                                    dynamicShader = null
                                ),
                                labelComponent = textComponent(
                                    color = ChartDistrictLine,
                                    textSize = 10.sp
                                ),
                                minimumLineThicknessDp = 1f
                            )
                        )
                    }
                    data.stateBenchmark?.let { threshold ->
                        add(
                            ThresholdLine(
                                thresholdValue = threshold.toFloat(),
                                lineComponent = shapeComponent(
                                    shape = Shapes.rectShape,
                                    color = ChartStateLine,
                                    dynamicShader = null
                                ),
                                labelComponent = textComponent(
                                    color = ChartStateLine,
                                    textSize = 10.sp
                                ),
                                minimumLineThicknessDp = 1f
                            )
                        )
                    }
                    data.nationalBenchmark?.let { threshold ->
                        add(
                            ThresholdLine(
                                thresholdValue = threshold.toFloat(),
                                lineComponent = shapeComponent(
                                    shape = Shapes.rectShape,
                                    color = ChartNationalLine,
                                    dynamicShader = null
                                ),
                                labelComponent = textComponent(
                                    color = ChartNationalLine,
                                    textSize = 10.sp
                                ),
                                minimumLineThicknessDp = 1f
                            )
                        )
                    }
                }
            ),
            chartModelProducer = chartEntryModelProducer,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        // Legend
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ChartLegendItem(color = ChartLine, label = "Performance")
            if (data.districtBenchmark != null) {
                ChartLegendItem(color = ChartDistrictLine, label = "District")
            }
            if (data.stateBenchmark != null) {
                ChartLegendItem(color = ChartStateLine, label = "State")
            }
            if (data.nationalBenchmark != null) {
                ChartLegendItem(color = ChartNationalLine, label = "National")
            }
        }
    }
}

@Composable
private fun ChartLegendItem(color: Color, label: String) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .padding(1.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
