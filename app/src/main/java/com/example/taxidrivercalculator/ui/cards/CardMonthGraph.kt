package com.example.taxidrivercalculator.ui.cards

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taxidrivercalculator.R
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.ViewRange
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class CardMonthGraph {

    @Composable
    fun DrawMonthGraphCard(chartData: StateFlow<List<Double>>, goalData: StateFlow<Double>) {
        val chartState by chartData.collectAsStateWithLifecycle()
        val goal by goalData.collectAsState()
        var max = chartState.maxOrNull() ?: 0.0 //TODO: goal
        val min = chartState.minOrNull()?.takeIf { it <= 0.0 } ?: 0.0
        val isDarkTheme = isSystemInDarkTheme()
        val textStyle: TextStyle = if (isDarkTheme) TextStyle(color = Color.White)
        else TextStyle(color = Color.Black)
        //val colorGraphLine = SolidColor(Color(0xFF23af92)) FBD323
        val colorGraphLine = Color(0xFFFBD323)

        if (max < goal) {
            max = goal
        }

        val axisProperties = AxisProperties(
            enabled = false,
            /*color = SolidColor(Color(0xFFFF0000)),
            style = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f)),
            color = SolidColor(Color(0xFF0000FF)),
            thickness = (.5).dp,
            lineCount = 5*/
        )
        val labelHelperProperties = LabelHelperProperties(
            enabled = true,
            textStyle = textStyle
        )
        val labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                color = Color(0xFFFF0000),
                fontSize = 12.sp,
                textAlign = TextAlign.End
            )

        )
        val gridProperties = GridProperties(
            enabled = false,
            yAxisProperties = axisProperties,
            xAxisProperties = axisProperties
        )
        val indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = textStyle,
        )
        val viewRange = ViewRange(0, LocalDate.now().dayOfMonth - 1)

        BaseCard {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Text(
                    text = stringResource(R.string.month_graph),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                LineChart(
                    data = remember {
                        listOf(
                            Line(
                                label = "Goal reach",
                                values = chartState,
                                color = SolidColor(colorGraphLine),
                                firstGradientFillColor = colorGraphLine.copy(alpha = .5f),
                                secondGradientFillColor = Color.Transparent,
                                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                gradientAnimationDelay = 1000,
                                drawStyle = ir.ehsannarmani.compose_charts.models.DrawStyle.Stroke(
                                    width = 2.dp
                                ),
                                viewRange = viewRange
                            ),
                            Line(
                                label = "Goal",
                                values = listOf(goal, goal),
                                color = SolidColor(Color(0xFFFF0000)),
                                //popupProperties = PopupProperties(enabled = false, duration = 0)
                            )
                        )
                    },
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
                    labelHelperProperties = labelHelperProperties,
                    labelProperties = labelProperties,
                    minValue = min,
                    maxValue = max * 1.0,
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .padding(top = 50.dp)
                )
            }
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
        DrawMonthGraphCard(previewData, MutableStateFlow(0.0))
    }
}