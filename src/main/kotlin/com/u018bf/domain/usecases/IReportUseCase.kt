package com.u018bf.domain.usecases

import com.u018bf.domain.models.DailyReport
import kotlinx.datetime.LocalDate

interface IReportUseCase {
    suspend fun listDailyReportByUserAndDates(from: LocalDate, to: LocalDate, userID: String): List<DailyReport>
}