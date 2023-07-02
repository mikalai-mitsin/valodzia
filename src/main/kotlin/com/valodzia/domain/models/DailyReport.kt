package com.valodzia.domain.models

import kotlinx.datetime.LocalDate
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

open class DailyReport(var date: LocalDate, var spend: List<com.valodzia.domain.models.IssueReport>, val workingDay: com.valodzia.domain.models.WorkingDay) {
    class FromIssues(date: LocalDate, issues: List<com.valodzia.domain.models.Issue>, workingDay: com.valodzia.domain.models.WorkingDay) : com.valodzia.domain.models.DailyReport(
        date,
        issues.filter { it.getDaySum(date).isPositive() }
            .map { com.valodzia.domain.models.IssueReport("${it.project}: ${it.title}", it.getDaySum(date)) },
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
    fun getLevel(): com.valodzia.domain.models.DailyReport.Level {
        return when (getTotal().inWholeMinutes) {
            in workingDay.duration.inWholeMinutes - 15..workingDay.duration.inWholeMinutes + 15 -> com.valodzia.domain.models.DailyReport.Level.OK
            in workingDay.duration.inWholeMinutes - 60..workingDay.duration.inWholeMinutes + 60 -> com.valodzia.domain.models.DailyReport.Level.WARNING
            else -> com.valodzia.domain.models.DailyReport.Level.BAD
        }
    }
}