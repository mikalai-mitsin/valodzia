package com.valodzia.domain.models

import kotlinx.datetime.LocalDate
import kotlin.time.Duration;

data class Spend(var duration: Duration, var userID: String, var issue: String, var date: LocalDate)