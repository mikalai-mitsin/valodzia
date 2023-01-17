package com.u018bf.domain.repositories

import com.u018bf.domain.models.WorkingDay

interface IWorkingDayRepository {
    suspend fun listByUserID(userID: String): List<WorkingDay>
}