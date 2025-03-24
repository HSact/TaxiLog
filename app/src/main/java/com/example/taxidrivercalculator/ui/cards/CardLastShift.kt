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


class CardLastShift {

    @Composable
    fun DrawLastShiftCard(date: String) {
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
                        Text(text = date)
                        Text(text = "Earnings")
                        Text(text = "Costs")
                        Text(text = "Time")
                        Text(text = "Total")
                        Text(text = "Per hour")
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BaseCardPreview() {
        DrawLastShiftCard("12.12.2022")
        }
}