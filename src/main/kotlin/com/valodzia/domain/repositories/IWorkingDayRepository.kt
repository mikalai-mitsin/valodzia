package com.valodzia.domain.repositories

import com.valodzia.domain.models.WorkingDay

interface IWorkingDayRepository {
    suspend fun listByUserID(userID: String): List<WorkingDay>
}