package com.hsact.taxilog.ui.fragments.goals

data class GoalDataState(
    val monthGoal: Double = 0.0,
    val weekGoal: Double = 0.0,
    val dayGoal: Double = 0.0,
    val dayProgress: Double = 0.0,
    val weekProgress: Double = 0.0,
    val monthProgress: Double = 0.0,
    val todayPercent: Double = 0.0,
    val weekPercent: Double = 0.0,
    val monthPercent: Double = 0.0
)