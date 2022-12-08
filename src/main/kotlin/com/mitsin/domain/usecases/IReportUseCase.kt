package com.mitsin.domain.usecases

import com.mitsin.domain.models.DailyReport
import kotlinx.datetime.LocalDate

interface IReportUseCase {
    suspend fun getDailyReportByUser(userID: String, date: LocalDate): DailyReport

    suspend fun listDailyReportByUserAndDates(from: LocalDate, to: LocalDate, userID: String): List<DailyReport>
}