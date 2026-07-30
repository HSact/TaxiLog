package com.hsact.taxilog.ui.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalConfiguration
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
import kotlin.math.roundToInt

@Composable
fun CardGoal(
    monthGoal: Float,
    shiftListFlow: StateFlow<List<Shift>>,
    onSetGoalClick: () -> Unit = {}
) {
    val shiftList = shiftListFlow.collectAsStateWithLifecycle().value
    val locale = LocalConfiguration.current.locales[0]
    val totalProfit = shiftList.totalProfit.toFloat() / 100
    
    val isGoalSet = monthGoal > 0f

    BaseCard {
        if (!isGoalSet) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CardHeader(stringResource(R.string.goal_per_month, ""))
                Spacer(Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.goals_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.goals_empty_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = onSetGoalClick) {
                    Text(stringResource(R.string.settings_label))
                }
            }
        } else {
            val rawProgress: Float = if (monthGoal != 0f) {
                (totalProfit / monthGoal).coerceIn(0f, 1f)
            } else {
                0f
            }
            var progress by remember { mutableFloatStateOf(0f) }
            val formatter = NumberFormat.getNumberInstance(locale).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }
            val formattedGoal = formatter.format(monthGoal)
            val formattedTotalProfit = formatter.format(totalProfit)
            
            LaunchedEffect(rawProgress) {
                progress = rawProgress
            }
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(durationMillis = 2000)
            )

            val stringOf = stringResource(R.string.of)
            val displayProgress = (progress * 1000).roundToInt() / 10f

            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CardHeader(stringResource(R.string.goal_per_month, formattedGoal))
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp),
                        text = "$formattedTotalProfit $stringOf $formattedGoal"
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(),
                        progress = { animatedProgress },
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 4.dp),
                        text = ("$displayProgress%")
                    )
                }
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