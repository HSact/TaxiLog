package com.hsact.taxilog.ui.fragments.log

import com.hsact.taxilog.R

/**
 * Enum representing the available sorting options for the shifts log.
 *
 * @property titleRes String resource ID for the sort option name.
 */
enum class LogSortOrder(val titleRes: Int) {
    DATE_DESC(R.string.sort_date_desc),
    DATE_ASC(R.string.sort_date_asc),
    PROFIT_DESC(R.string.sort_profit_desc),
    PROFIT_ASC(R.string.sort_profit_asc),
    DURATION_DESC(R.string.sort_duration_desc),
    DURATION_ASC(R.string.sort_duration_asc),
}
