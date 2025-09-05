package com.hsact.taxilog.ui.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsact.domain.model.Shift
import com.hsact.domain.utils.totalProfit
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.components.CardHeader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun CardGoal(
    monthGoal: Float,
    shiftListFlow: StateFlow<List<Shift>>,
) {
    val goal = monthGoal
    val shiftList = shiftListFlow.collectAsStateWithLifecycle().value
    val totalProfit = shiftList.totalProfit.toFloat() / 100
    val rawProgress: Float = if (goal != 0f) {
        (totalProfit / goal).coerceIn(0f, 1f)
    } else {
        0f
    }
    var progress by remember { mutableFloatStateOf(0f) }
    var formattedGoal = NumberFormat.getNumberInstance(Locale.getDefault()).format(goal)
    val formattedTotalProfit =
        NumberFormat.getNumberInstance(Locale.getDefault()).format(totalProfit)
    if (goal == 1f) {
        formattedGoal = "N/A"
    }
    LaunchedEffect(rawProgress) {
        progress = rawProgress
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000)
    )

    val stringOf = stringResource(R.string.of)
    progress = (progress * 1000).roundToInt() / 1000f
    BaseCard {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CardHeader(stringResource(R.string.goal_per_month, formattedGoal))
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    text = "$formattedTotalProfit $stringOf $formattedGoal"
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(),
                    progress = { animatedProgress },
                    color = Color(0xFFE8BD00),
                    trackColor = Color(0xFFFFF8D9)
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = ("${"%.1f".format(progress * 100)}%")
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardPreview() {
    val goal = "100"
    val goalCurrent = "50"
    remember {
        MutableStateFlow(
            mapOf(
                "goal" to goal,
                "goalCurrent" to goalCurrent
            )
        )
    }
}