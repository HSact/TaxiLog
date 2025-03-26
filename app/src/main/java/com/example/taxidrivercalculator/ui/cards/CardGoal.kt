package com.example.taxidrivercalculator.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taxidrivercalculator.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

class CardGoal{

    @Composable
    fun DrawGoalCard(shiftData: StateFlow<Map<String, String>>) {
        val shiftState by shiftData.collectAsStateWithLifecycle()
        val goal = shiftState["goal"]?:""
        val goalCurrent = shiftState["goalCurrent"]?:""
        var progress = goalCurrent.toFloat() / goal.toFloat()
        progress = (progress * 1000).roundToInt() / 1000f
        BaseCard {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Text(
                    text = stringResource(R.string.your_goal_per_month, goal),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = "$goalCurrent of $goal"
                        )
                        LinearProgressIndicator(
                            modifier = Modifier.height(10.dp).fillMaxWidth(),
                            progress = {progress},
                            color = Color(0xFFE8BD00),
                            trackColor = Color(0xFFFFF8D9)
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = (progress * 100).toString() + "%")
                    }
            }
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
        DrawGoalCard(previewData)
    }
}