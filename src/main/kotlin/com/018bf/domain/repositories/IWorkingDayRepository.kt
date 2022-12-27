package com.`018bf`.domain.repositories

import com.`018bf`.domain.models.Issue
import com.`018bf`.domain.models.WorkingDay

interface IWorkingDayRepository {
    suspend fun listByUserID(userID: String): List<WorkingDay>
}