package com.hsact.taxilog.ui.cards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsact.domain.model.Shift
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.components.CardHeader
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields

@Composable
fun CardCalendar(
    calendarMonthFlow: StateFlow<YearMonth>,
    shiftsFlow: StateFlow<List<Shift>>,
    firstDayOfWeek: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onShiftsClick: (List<Shift>) -> Unit,
) {
    val month by calendarMonthFlow.collectAsStateWithLifecycle()
    val shiftList by shiftsFlow.collectAsStateWithLifecycle()

    val locale = LocalConfiguration.current.locales[0]
    val monthName =
        month.month
            .getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    val year = month.year

    BaseCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardHeader(text = stringResource(R.string.calendar))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text(
                    text = "$monthName $year",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CalendarGrid(month, shiftList, firstDayOfWeek, onShiftsClick)
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    shifts: List<Shift>,
    firstDayOfWeekSetting: Int,
    onShiftsClick: (List<Shift>) -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val firstDayOfWeek =
        if (firstDayOfWeekSetting > 0) {
            DayOfWeek.of(firstDayOfWeekSetting)
        } else {
            WeekFields.of(locale).firstDayOfWeek
        }
    val daysInMonth = month.lengthOfMonth()
    val firstDayOfMonth = month.atDay(1).dayOfWeek

    val scale = remember { Animatable(0f) }

    LaunchedEffect(month) {
        scale.snapTo(0f)
        scale.animateTo(
            targetValue = 1f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
        )
    }

    // Calculate offset based on firstDayOfWeek
    val offset = ((firstDayOfMonth.value - firstDayOfWeek.value) + 7) % 7

    val daysOfWeek =
        remember(firstDayOfWeek) {
            (0..6).map { firstDayOfWeek + it.toLong() }
        }

    // Pre-calculate shifts by date for performance
    val shiftsByDate =
        remember(shifts) {
            shifts.groupBy {
                it.time.period.start
                    .toLocalDate()
            }
        }

    Column {
        // Day labels
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, locale).uppercase(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days
        val totalCells = offset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - offset + 1

                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (dayNum in 1..daysInMonth) {
                            val date = month.atDay(dayNum)
                            val dayShifts = shiftsByDate[date] ?: emptyList()
                            val hasShift = dayShifts.isNotEmpty()

                            if (hasShift) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(36.dp)
                                            .graphicsLayer {
                                                scaleX = scale.value
                                                scaleY = scale.value
                                            }.background(MaterialTheme.colorScheme.secondary, CircleShape)
                                            .clickable { onShiftsClick(dayShifts) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = dayNum.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            } else {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
