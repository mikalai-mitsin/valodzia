package com.`018bf`.domain.models

import kotlinx.datetime.LocalDate
import space.jetbrains.api.runtime.types.MessageStyle
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

open class DailyReport(var date: LocalDate, var spend: List<IssueReport>, val workingDay: WorkingDay) {
    class FromIssues(date: LocalDate, issues: List<Issue>, workingDay: WorkingDay) : DailyReport(
        date,
        issues.filter { it.getDaySum(date).isPositive() }
            .map { IssueReport("${it.project}: ${it.title}", it.getDaySum(date)) },
        workingDay,
    )
    enum class Level {
        OK,
        WARNING,
        BAD
    }

    fun getTotal(): Duration {
        return spend.sumOf { it.spend.toInt(DurationUnit.MINUTES) }.toDuration(DurationUnit.MINUTES)
    }

    override fun toString(): String {
        var body = "${this.date} - *${this.date.dayOfWeek}* \n\n| Issue | Spend |\n| ------ | ------ |\n"
        body += this.spend.joinToString(
            prefix = "",
            separator = "\n",
            postfix = "\n"
        ) { "| ${it.title} | ${it.spend} |" }
        body += "| Total | **${this.getTotal()}** |\n"
        return body
    }
    fun getLevel(): Level {
        return when (getTotal().inWholeMinutes) {
            in workingDay.duration.inWholeMinutes - 15..workingDay.duration.inWholeMinutes + 15 -> Level.OK
            in workingDay.duration.inWholeMinutes - 60..workingDay.duration.inWholeMinutes + 60 -> Level.WARNING
            else -> Level.BAD
        }
    }
}