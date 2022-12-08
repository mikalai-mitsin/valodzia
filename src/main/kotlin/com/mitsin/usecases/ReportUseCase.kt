package com.mitsin.usecases

import kotlinx.datetime.*
import com.mitsin.domain.models.DailyReport
import com.mitsin.domain.models.IssueReport
import com.mitsin.domain.repositories.IIssueRepository
import com.mitsin.domain.usecases.IReportUseCase


class ReportUseCase(private val issueRepository: IIssueRepository) : IReportUseCase {

    override suspend fun getDailyReportByUser(userID: String, date: LocalDate): DailyReport {
        val issues = issueRepository.getIssueByProjectAndUser(userID)
        return DailyReport(
            date = date,
            spend = issues.filter { it.getDaySum(date).isPositive() }
                .map { IssueReport("${it.project}: ${it.title}", it.getDaySum(date)) }
        )
    }

    override suspend fun listDailyReportByUserAndDates(from: LocalDate, to: LocalDate, userID: String): List<DailyReport> {
        val issues = issueRepository.getIssueByProjectAndUser(userID)
        var date = from
        val reports = emptyList<DailyReport>().toMutableList()
        while (date.until(to, DateTimeUnit.DAY) >= 0) {
            reports += DailyReport.FromIssues(date, issues)
            date = date.plus(DatePeriod(0, 0, 1))
        }
        return reports
    }
}