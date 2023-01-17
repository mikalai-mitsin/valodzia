package com.u018bf.domain.models

import kotlinx.datetime.LocalDate
import org.ktorm.schema.Table
import org.ktorm.schema.varchar
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Issue(val id: String, val title: String, val project: String, val spends: List<Spend>) {
    fun getDaySum(date: LocalDate): Duration {
        val count = this.spends.filter { it.date == date }.sumOf { it.duration.toInt(DurationUnit.MINUTES) }
        return count.toDuration(DurationUnit.MINUTES)
    }
}