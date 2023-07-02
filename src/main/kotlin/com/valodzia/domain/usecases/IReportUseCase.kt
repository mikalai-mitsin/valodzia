package com.valodzia.domain.usecases

import com.valodzia.domain.models.DailyReport
import com.valodzia.domain.models.MonthlyReport
import kotlinx.datetime.LocalDate

interface IReportUseCase {
    suspend fun getDailyReportByUser(userID: String, date: LocalDate): com.valodzia.domain.models.DailyReport
    suspend fun getMonthlyReportByUser(userID: String, date: LocalDate): MonthlyReport

    suspend fun listDailyReportByUserAndDates(from: LocalDate, to: LocalDate, userID: String): List<com.valodzia.domain.models.DailyReport>
}