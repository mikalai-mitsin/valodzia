package com.u018bf.domain.usecases

import com.u018bf.domain.models.DailyReport
import kotlinx.datetime.LocalDate

interface IIssueUseCase {
    suspend fun sync()
}