package com.`018bf`.usecases

import kotlinx.datetime.*
import com.`018bf`.domain.models.DailyReport
import com.`018bf`.domain.models.IssueReport
import com.`018bf`.domain.repositories.IIssueRepository
import com.`018bf`.domain.repositories.IWorkingDayRepository
import com.`018bf`.domain.usecases.IReportUseCase
import space.jetbrains.api.runtime.types.MessageStyle


class ReportUseCase(
    private val issueRepository: IIssueRepository,
    private val workingDayRepository: IWorkingDayRepository
) : IReportUseCase {

    override suspend fun getDailyReportByUser(userID: String, date: LocalDate): DailyReport {
        val days = workingDayRepository.listByUserID(userID)
        val issues = issueRepository.getIssueByProjectAndUser(userID)
        return DailyReport(
            date = date,
            spend = issues.filter { it.getDaySum(date).isPositive() }
                .map { IssueReport("${it.project}: ${it.title}", it.getDaySum(date)) },
            workingDay = days.first { it.day == date.dayOfWeek }
        )
    }

    override suspend fun listDailyReportByUserAndDates(
        from: LocalDate,
        to: LocalDate,
        userID: String
    ): List<DailyReport> {
        val days = workingDayRepository.listByUserID(userID)
        val issues = issueRepository.getIssueByProjectAndUser(userID)
        var date = from
        val reports = emptyList<DailyReport>().toMutableList()
        while (date.until(to, DateTimeUnit.DAY) >= 0) {
            reports += DailyReport.FromIssues(date, issues, days.first { it.day == date.dayOfWeek })
            date = date.plus(DatePeriod(0, 0, 1))
        }
        return reports
    }
}