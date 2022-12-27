package com.`018bf`.domain.models

import kotlinx.datetime.TimeZone
import java.time.DayOfWeek
import kotlin.time.Duration

data class WorkingDay(val day: DayOfWeek, val duration: Duration)
