package com.valodzia.domain.models

import kotlinx.datetime.LocalDate
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class Issue(val id: String, val title: String, val project: String, val spends: List<Spend>) {
    fun getDaySum(date: LocalDate): Duration {
        val count = this.spends.filter { it.date == date }.sumOf { it.duration.toInt(DurationUnit.MINUTES) }
        return count.toDuration(DurationUnit.MINUTES)
    }

    fun getMonthSum(date: LocalDate): Duration {
        val count = this.spends.filter { it.date.monthNumber == date.monthNumber }.sumOf { it.duration.toInt(DurationUnit.MINUTES) }
        return count.toDuration(DurationUnit.MINUTES)
    }
}