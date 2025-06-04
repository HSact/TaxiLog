package com.hsact.taxilog.data.utils

import java.time.format.DateTimeFormatter

object DeprecatedDateFormatter {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
}