package com.example.taxidrivercalculator.ui.cards

import android.widget.ProgressBar
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taxidrivercalculator.R
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CardGoal{

    @Composable
    fun DrawGoalCard(shiftData: StateFlow<Map<String, String>>) {
        val shiftState by shiftData.collectAsStateWithLifecycle()
        val goal = shiftState["goal"]?:""
        val goalCurrent = shiftState["goalCurrent"]?:""
        val progress = goalCurrent.toFloat()/goal.toFloat()
        BaseCard {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Text(
                    text = stringResource(R.string.your_goal_per_month, goal),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = goalCurrent)
                    }
                    Column {
                        LinearProgressIndicator(
                            progress = progress,
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                    Column {
                        Text(text = goal)
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BaseCardPreview() {
        val na = "N/A"
        val goal = "100000"
        val goalCurrent = "50000"
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