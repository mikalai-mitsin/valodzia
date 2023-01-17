package com.u018bf.usecases

import kotlinx.datetime.*
import com.u018bf.domain.models.DailyReport
import com.u018bf.domain.repositories.IIssueRepository
import com.u018bf.domain.repositories.IWorkingDayRepository
import com.u018bf.domain.usecases.IReportUseCase


class ReportUseCase(
    private val issueRepository: IIssueRepository,
    private val workingDayRepository: IWorkingDayRepository,
) : IReportUseCase {
    override suspend fun listDailyReportByUserAndDates(
        from: LocalDate,
        to: LocalDate,
        userID: String
    ): List<DailyReport> {
        val days = workingDayRepository.listByUserID(userID)
        val issues = issueRepository.list(userID, from, to)
        var date = from
        val reports = emptyList<DailyReport>().toMutableList()
        while (date.until(to, DateTimeUnit.DAY) >= 0) {
            reports += DailyReport.FromIssues(date, issues, days.first { it.day == date.dayOfWeek })
            date = date.plus(DatePeriod(0, 0, 1))
        }
        return reports
    }
}