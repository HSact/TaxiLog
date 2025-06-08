package com.hsact.taxilog.ui.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsact.taxilog.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private var stringOf = ""
private var stringDay = ""
private var stringWeek = ""
private var stringMonth = ""

@Composable
fun DrawDayWeekMonthProgressCard(goalData: StateFlow<Map<String, Double>>) {
    stringOf = stringResource(R.string.of)
    stringDay = stringResource(R.string.day)
    stringWeek = stringResource(R.string.this_week)
    stringMonth = stringResource(R.string.this_month)

    val goalState by goalData.collectAsStateWithLifecycle()

    val progressDayP = (goalState["todayPercent"] ?: 0f).toFloat()
    val progressWeekP = (goalState["weekPercent"] ?: 0f).toFloat()
    val progressMonthP = (goalState["monthPercent"] ?: 0f).toFloat()

    val progressDay = (goalState["dayProgress"] ?: 0f).toFloat()
    val progressWeek = (goalState["weekProgress"] ?: 0f).toFloat()
    val progressMonth = (goalState["monthProgress"] ?: 0f).toFloat()

    val goalMonth = (goalState["monthGoal"] ?: 0f).toFloat()
    val goalWeek = (goalState["weekGoal"] ?: 0f).toFloat()
    val goalDay = (goalState["dayGoal"] ?: 0f).toFloat()

    val animationDuration = 2000

    val progressDayAnimated = remember { mutableFloatStateOf(0f) }
    val progressWeekAnimated = remember { mutableFloatStateOf(0f) }
    val progressMonthAnimated = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(progressDayP) {
        progressDayAnimated.floatValue = progressDayP / 100
    }
    LaunchedEffect(progressWeekP) {
        progressWeekAnimated.floatValue = progressWeekP / 100
    }
    LaunchedEffect(progressMonthP) {
        progressMonthAnimated.floatValue = progressMonthP / 100
    }

    val animatedProgressDay = animateFloatAsState(
        targetValue = progressDayAnimated.floatValue.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = animationDuration)
    )

    val animatedProgressWeek = animateFloatAsState(
        targetValue = progressWeekAnimated.floatValue.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = animationDuration)
    )

    val animatedProgressMonth = animateFloatAsState(
        targetValue = progressMonthAnimated.floatValue.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = animationDuration)
    )

    BaseCard {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
            Column {
                Text(
                    text = stringResource(R.string.month_graph, goalMonth),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(bottom = 5.dp)
                )
                ProgressColumn(
                    stringDay,
                    goalDay,
                    animatedProgressDay.value,
                    progressDay,
                    progressDayP
                )
                ProgressColumn(
                    stringWeek,
                    goalWeek,
                    animatedProgressWeek.value,
                    progressWeek,
                    progressWeekP
                )
                ProgressColumn(
                    stringMonth,
                    goalMonth,
                    animatedProgressMonth.value,
                    progressMonth,
                    progressMonthP
                )
            }
        }
    }
}

@Composable
private fun ProgressColumn(
    period: String,
    goal: Float,
    animatedProgress: Float,
    progress: Float,
    progressP: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Start),
            text = period
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = "$progress $stringOf $goal"
        )
        LinearProgressIndicator(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth(),
            progress = { animatedProgress },
            color = Color(0xFFE8BD00),
            trackColor = Color(0xFFFFF8D9)
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = ("${"%.1f".format(progressP)}%")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardPreview() {
    val goal = 100.0
    val goalCurrent = 50.0
    val previewData = remember {
        MutableStateFlow(
            mapOf(
                "todayPercent" to 0.5,
                "weekPercent" to 0.6,
                "monthPercent" to 0.75,
                "monthGoal" to goal,
                "weekGoal" to goal,
                "dayGoal" to goal
            )
        )
    }
    DrawDayWeekMonthProgressCard(previewData)
}