package com.hsact.taxilog.ui.fragments.log

import com.hsact.taxilog.R

/**
 * Enum representing the time periods for filtering shifts in the log.
 *
 * @property titleRes String resource ID for the period name.
 */
enum class LogFilterPeriod(val titleRes: Int) {
    WEEK(R.string.filter_period_week),
    MONTH(R.string.filter_period_month),
    YEAR(R.string.filter_period_year),
    ALL(R.string.filter_period_all),
}
