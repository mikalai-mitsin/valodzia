package com.`018bf`.domain.models

import kotlinx.datetime.LocalDate
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

open class DailyReport(var date: LocalDate, var spend: List<IssueReport>) {
    class FromIssues(date: LocalDate, issues: List<Issue>) : DailyReport(
        date,
        issues.filter { it.getDaySum(date).isPositive() }
            .map { IssueReport("${it.project}: ${it.title}", it.getDaySum(date)) }
    )

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
}