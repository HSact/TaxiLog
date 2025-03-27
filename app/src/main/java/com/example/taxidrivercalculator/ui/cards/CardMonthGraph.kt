package com.example.taxidrivercalculator.ui.cards

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.snackbar.BaseTransientBottomBar.AnimationMode
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CardMonthGraph {

    @Composable
    fun DrawMonthGraphCard(chartData: StateFlow<List<Double>>) {
        val chartState by chartData.collectAsStateWithLifecycle()
        val max = chartState.maxOrNull() ?: 0.0 //TODO: goal
        val min = chartState.minOrNull() ?: 0.0
        val isDarkTheme = isSystemInDarkTheme()

        val axisProperties = AxisProperties(
            enabled = false,
            color = SolidColor(Color(0xFFFF0000)),
            /*style = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f)),
            color = SolidColor(Color(0xFF0000FF)),
            thickness = (.5).dp,
            lineCount = 5*/
        )
        val labelProperties = LabelProperties(
            enabled = false,
            textStyle = TextStyle(color = Color.Blue)
        )
        val gridProperties = GridProperties(
            enabled = false,
            yAxisProperties = axisProperties,
            xAxisProperties = axisProperties
        )
        val indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = if (isDarkTheme) TextStyle(color = Color.White)
            else TextStyle(color = Color.Black),
        )

        BaseCard {
            LineChart(
                data = remember {
                    listOf(
                        Line(
                            label = "Goal reach",
                            values = chartState,
                            color = SolidColor(Color(0xFF23af92)),
                            firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                            gradientAnimationDelay = 1000,
                            drawStyle = ir.ehsannarmani.compose_charts.models.DrawStyle.Stroke(
                                width = 2.dp
                            )
                        )
                        /*Line(
                        label = "Goal",
                        values = listOf(0.0, 0.0),
                        color = SolidColor(Color(0xFFFF0000)),
                    )*/
                    )
                },
                //lineProperties = labelProperties,
                animationMode = ir.ehsannarmani.compose_charts.models.AnimationMode.Together(
                    delayBuilder = {
                        it * 500L
                    }),
                gridProperties = gridProperties,
                zeroLineProperties = ZeroLineProperties(
                    enabled = false,
                    //color = SolidColor(Color(0xFFFFFFFF)),
                ),
                indicatorProperties = indicatorProperties,
                labelProperties = labelProperties,
                minValue = min,
                maxValue = max * 1.2,
                modifier = Modifier.heightIn(max = 300.dp)
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CardPreview() {

        val previewData = remember {
            MutableStateFlow(
                listOf(
                    0.0, 2.0, 3.0, 7.0, 10.0, 12.0, 18.0, 25.0, 27.0, 30.0
                )
            )
        }
        DrawMonthGraphCard(previewData)
    }
}