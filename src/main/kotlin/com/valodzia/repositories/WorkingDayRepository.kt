package com.valodzia.repositories

import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.resources.teamDirectory
import space.jetbrains.api.runtime.types.*
import com.valodzia.domain.models.WorkingDay
import com.valodzia.domain.repositories.IWorkingDayRepository
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class WorkingDayRepository(private val space: SpaceClient): IWorkingDayRepository {
    override suspend fun listByUserID(userID: String): List<WorkingDay> {
        val response = space.teamDirectory.profiles.workingDays.queryWorkingDaysForAProfile(
            profile = ProfileIdentifier.Id(userID)
        ) {
            workingDaysSpec()
        }
        val r = response.data.first()
        return r.workingDaysSpec.workingHours?.map { dtw(it)}?.toList() ?: emptyList()
    }
}

fun dtw(it: WeekDayTimeInterval): WorkingDay {
    var day = it.day
    if (day == 0) {
        day = 7
    }
    if (!it.checked) {
        return WorkingDay(DayOfWeek.of(day), 0.toDuration(DurationUnit.SECONDS))
    }
    val since = LocalTime.of(it.interval.since.hours,  it.interval.since.minutes,  0)
    val till = LocalTime.of(it.interval.till.hours,  it.interval.till.minutes,  0)
    val duration = (till.toSecondOfDay() - since.toSecondOfDay()).toDuration(DurationUnit.SECONDS)
    return WorkingDay(DayOfWeek.of(day), duration)
}