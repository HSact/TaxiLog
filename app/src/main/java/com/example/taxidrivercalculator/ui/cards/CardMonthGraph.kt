package com.example.taxidrivercalculator.ui.cards

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.snackbar.BaseTransientBottomBar.AnimationMode
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.stream.DoubleStream.builder
import kotlin.math.roundToInt

class CardMonthGraph {

    @Composable
    fun DrawMonthGraphCard(shiftData: StateFlow<Map<String, String>>) {
        val shiftState by shiftData.collectAsStateWithLifecycle()
        val goal = shiftState["goal"] ?: ""
        val goalCurrent = shiftState["goalCurrent"] ?: ""
        var progress = goalCurrent.toFloat() / goal.toFloat()
        progress = (progress * 1000).roundToInt() / 1000f
        val axisProperties = AxisProperties(
            enabled = true,
            style = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f)),
            color = SolidColor(Color(0xFFFFFFFF)),
            thickness = (.5).dp,
            lineCount = 5
        )
        val labelProperties = LabelProperties(
            enabled = true,
            textStyle = MaterialTheme.typography.labelSmall
        )
        val gridProperties = GridProperties(
            enabled = false
        )

        BaseCard {
            LineChart(
                data = remember {
                    listOf(
                        Line(
                            label = "Goal reach",
                            values = listOf(28.0, -11.0, 5.0, 10.0, 35.0),
                            color = SolidColor(Color(0xFF23af92)),
                        ),
                        Line(
                            label = "Zero",
                            values = listOf(0.0, 0.0),
                            color = SolidColor(Color(0xFFFF0000)),
                        )
                    )
                },
                gridProperties = gridProperties,
                zeroLineProperties = ZeroLineProperties(
                    enabled = true,
                    color = SolidColor(Color(0xFFFFFFFF)),
                ),
                labelProperties = labelProperties,
                minValue = -20.0,
                maxValue = 100.0,
                modifier = Modifier.heightIn(max = 300.dp)
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CardPreview() {
        val na = "N/A"
        val goal = "100"
        val goalCurrent = "50"
        val previewData = remember {
            MutableStateFlow(
                mapOf(
                    "goal" to goal,
                    "goalCurrent" to goalCurrent
                )
            )
        }
        DrawMonthGraphCard(previewData)
    }
}