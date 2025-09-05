package com.hsact.taxilog.ui.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hsact.taxilog.ui.components.CardHeader
import com.hsact.taxilog.ui.fragments.goals.GoalDataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.NumberFormat
import java.util.Locale

private var stringOf = ""
private var stringDay = ""
private var stringWeek = ""
private var stringMonth = ""

@Composable
fun CardDayWeekMonthProgress(goalData: StateFlow<GoalDataState>) {
    stringOf = stringResource(R.string.of)
    stringDay = stringResource(R.string.day)
    stringWeek = stringResource(R.string.this_week)
    stringMonth = stringResource(R.string.this_month)

    val goalState by goalData.collectAsStateWithLifecycle()

    val progressDayP = goalState.todayPercent.toFloat()
    val progressWeekP = goalState.weekPercent.toFloat()
    val progressMonthP = goalState.monthPercent.toFloat()

    val progressDay = goalState.dayProgress.toFloat()
    val progressWeek = goalState.weekProgress.toFloat()
    val progressMonth = goalState.monthProgress.toFloat()

    val goalMonth = goalState.monthGoal.toFloat()
    val goalWeek = goalState.weekGoal.toFloat()
    val goalDay = goalState.dayGoal.toFloat()

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
        Column {
            CardHeader(stringResource(R.string.month_graph, goalMonth))
            Spacer(modifier = Modifier.height(8.dp))
            GoalProgressIndicator(
                stringDay,
                goalDay,
                animatedProgressDay.value,
                progressDay,
                progressDayP
            )
            GoalProgressIndicator(
                stringWeek,
                goalWeek,
                animatedProgressWeek.value,
                progressWeek,
                progressWeekP
            )
            GoalProgressIndicator(
                stringMonth,
                goalMonth,
                animatedProgressMonth.value,
                progressMonth,
                progressMonthP
            )
        }
    }
}

@Composable
private fun GoalProgressIndicator(
    period: String,
    goal: Float,
    animatedProgress: Float,
    progress: Float,
    progressP: Float,
) {
    val formattedProgress = NumberFormat.getNumberInstance(Locale.getDefault()).format(progress)
    val formattedGoal = NumberFormat.getNumberInstance(Locale.getDefault()).format(goal)
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
            text = "$formattedProgress $stringOf $formattedGoal"
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
            GoalDataState(
                dayProgress = goalCurrent,
                weekProgress = goalCurrent,
                monthProgress = goalCurrent,
                dayGoal = goal,
                weekGoal = goal,
                monthGoal = goal,
                todayPercent = 70.0,
                weekPercent = 60.0,
                monthPercent = 40.0
            )
        )
    }
    CardDayWeekMonthProgress(previewData)
}