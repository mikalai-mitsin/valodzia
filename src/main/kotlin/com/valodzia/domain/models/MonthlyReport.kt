package com.valodzia.domain.models

import kotlinx.datetime.LocalDate
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

open class MonthlyReport(var date: LocalDate, var spend: List<IssueReport>) {
    class FromIssues(date: LocalDate, issues: List<Issue>) : MonthlyReport(
        date,
        issues.filter { it.getMonthSum(date).isPositive() }
            .map { IssueReport("${it.project}: ${it.title}", it.getMonthSum(date)) }
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