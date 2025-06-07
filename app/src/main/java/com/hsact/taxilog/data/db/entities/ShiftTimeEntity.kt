package com.hsact.taxilog.data.db.entities


class ShiftTimeEntity(
    val period: DateTimePeriodEntity,
    val rest: DateTimePeriodEntity? = null,
)