package com.valodzia.domain.models

import kotlinx.datetime.TimeZone
import java.time.DayOfWeek
import kotlin.time.Duration

data class WorkingDay(val day: DayOfWeek, val duration: Duration)
