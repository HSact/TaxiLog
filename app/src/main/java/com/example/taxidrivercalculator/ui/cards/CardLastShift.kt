package com.example.taxidrivercalculator.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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


class CardLastShift {

    @Composable
    fun DrawLastShiftCard(shiftData: StateFlow<Map<String, String>>) {
        val shiftState by shiftData.collectAsStateWithLifecycle()
        BaseCard {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Text(
                    text = stringResource(R.string.last_shift),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = stringResource(R.string.date))
                        Text(text = stringResource(R.string.earnings))
                        Text(text = stringResource(R.string.costs))
                        Text(text = stringResource(R.string.time))
                        Text(text = stringResource(R.string.total))
                        Text(text = stringResource(R.string.per_hour))
                    }
                    Column {
                        Text(text = shiftState["date"] ?: "")
                        Text(text = shiftState["earnings"] ?: "")
                        Text(text = shiftState["costs"] ?: "")
                        Text(text = shiftState["time"] ?: "")
                        Text(text = shiftState["total"] ?: "")
                        Text(text = shiftState["perHour"] ?: "")
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BaseCardPreview() {
        val na = "N/A"
        val previewData = remember {
            MutableStateFlow(
                mapOf(
                    "date" to na,
                    "earnings" to na,
                    "costs" to na,
                    "time" to na,
                    "total" to na,
                    "perHour" to na
                )
            )
        }
        DrawLastShiftCard(previewData)
    }
}