package com.valodzia.usecases

import kotlinx.datetime.*
import com.valodzia.domain.models.DailyReport
import com.valodzia.domain.models.MonthlyReport
import com.valodzia.domain.repositories.IIssueRepository
import com.valodzia.domain.repositories.IWorkingDayRepository
import com.valodzia.domain.usecases.IReportUseCase


class ReportUseCase(
    private val issueRepository: IIssueRepository,
    private val workingDayRepository: IWorkingDayRepository
) : IReportUseCase {

    override suspend fun getDailyReportByUser(userID: String, date: LocalDate): com.valodzia.domain.models.DailyReport {
        val days = workingDayRepository.listByUserID(userID)
        val issues = issueRepository.getIssueByProjectAndUser(userID)
        return com.valodzia.domain.models.DailyReport.FromIssues(date, issues, days.first { it.day == date.dayOfWeek })
    }
    override suspend fun getMonthlyReportByUser(userID: String, date: LocalDate): MonthlyReport {
        val issues = issueRepository.getIssueByProjectAndUser(userID)
        return MonthlyReport.FromIssues(date, issues)
    }

    override suspend fun listDailyReportByUserAndDates(
        from: LocalDate,
        to: LocalDate,
        userID: String
    ): List<com.valodzia.domain.models.DailyReport> {
        val days = workingDayRepository.listByUserID(userID)
        val issues = issueRepository.getIssueByProjectAndUser(userID)
        var date = from
        val reports = emptyList<com.valodzia.domain.models.DailyReport>().toMutableList()
        while (date.until(to, DateTimeUnit.DAY) >= 0) {
            reports += com.valodzia.domain.models.DailyReport.FromIssues(date, issues, days.first { it.day == date.dayOfWeek })
            date = date.plus(DatePeriod(0, 0, 1))
        }
        return reports
    }
}