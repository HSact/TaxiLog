package com.hsact.taxilog.ui.fragments.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hsact.taxilog.R

/**
 * Horizontal bar with chips for filtering by period and opening the sort menu.
 *
 * @param currentPeriod The currently active filter period.
 * @param onPeriodSelected Callback when a filter period is selected.
 * @param onSortClick Callback when the sort icon is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterSortBar(
    currentPeriod: LogFilterPeriod,
    onPeriodSelected: (LogFilterPeriod) -> Unit,
    onSortClick: () -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = false,
                onClick = onSortClick,
                label = { Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null) },
            )
        }
        items(LogFilterPeriod.entries) { period ->
            FilterChip(
                selected = currentPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(stringResource(period.titleRes)) },
            )
        }
    }
}

/**
 * Bottom sheet for selecting the sort order of shifts.
 *
 * @param currentSort The currently active sort order.
 * @param onSortSelected Callback when a sort order is selected.
 * @param onDismiss Callback to close the bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SortBottomSheet(
    currentSort: LogSortOrder,
    onSortSelected: (LogSortOrder) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
            )
            LogSortOrder.entries.forEach { sortOrder ->
                ListItem(
                    headlineContent = { Text(stringResource(sortOrder.titleRes)) },
                    leadingContent = {
                        RadioButton(
                            selected = currentSort == sortOrder,
                            onClick = null,
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier =
                        Modifier.selectable(
                            selected = currentSort == sortOrder,
                            onClick = { onSortSelected(sortOrder) },
                        ),
                )
            }
        }
    }
}
