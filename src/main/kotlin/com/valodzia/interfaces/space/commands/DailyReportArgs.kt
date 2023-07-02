package com.valodzia.interfaces.space.commands

import kotlinx.datetime.LocalDate

class DailyReportArgs(
    val from: LocalDate,
    val to: LocalDate,
)
