package com.hsact.taxilog.ui.cards

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsact.domain.utils.DeprecatedDateFormatter
import com.hsact.taxilog.ui.components.CardHeader
import com.hsact.taxilog.ui.fragments.goals.DaysInMonthCardState
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DaysInMonthCard(data: StateFlow<DaysInMonthCardState>) {
    val dataState by data.collectAsStateWithLifecycle()
    val days = dataState.days
    val date = dataState.date
    val isDarkTheme = isSystemInDarkTheme()
    val textStyle: TextStyle = if (isDarkTheme) TextStyle(color = Color.White)
    else TextStyle(color = Color.Black)

    if (days.isEmpty()) {
        return
    }

    val colorGraphLine = Color(0xFFFBD323)
    val labelProperties = LabelProperties(
        enabled = true,
        textStyle = textStyle,
        rotation = LabelProperties.Rotation(degree = 0f)
    )
    val labelHelperProperties = LabelHelperProperties(
        enabled = false,
        textStyle = textStyle
    )
    val gridProperties = GridProperties(
        enabled = false,
    )
    val indicatorProperties = HorizontalIndicatorProperties(
        enabled = true,
        textStyle = textStyle,
    )
    val locale = Locale.getDefault()
    val inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    val parsedDate = LocalDate.parse(date, inputFormatter)
    var currentMonth = parsedDate.format(DeprecatedDateFormatter)
    if (locale.language == "ru") {
        currentMonth = currentMonth.replaceFirstChar { it.uppercase(locale) }
    }
    val bars =
        List(parsedDate.lengthOfMonth()) { index ->
            Bars(
                label = if (index % 2 == 0) (index + 1).toString() else " ",
                values = listOf(
                    Bars.Data(value = days[index], color = SolidColor(colorGraphLine))
                )
            )
        }

    BaseCard {
        CardHeader(currentMonth)
        ColumnChart(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .padding(top = 52.dp),
            data =
                bars,
            labelProperties = labelProperties,
            barProperties = BarProperties(
                thickness = 5.dp,
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                spacing = 1.dp,
            ),
            indicatorProperties = indicatorProperties,
            gridProperties = gridProperties,
            labelHelperProperties = labelHelperProperties,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardPreview() {
    val previewState = remember {
        MutableStateFlow(
            DaysInMonthCardState(
                date = "01.01.2020",
                days = listOf(
                    0.0, 2.0, 3.0, 7.0, 10.0, 12.0, 18.0, 25.0, 27.0, 30.0
                )
            )
        )
    }
    DaysInMonthCard(previewState)
}