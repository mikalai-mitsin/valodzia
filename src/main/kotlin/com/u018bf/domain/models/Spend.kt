package com.u018bf.domain.models

import kotlinx.datetime.LocalDate
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import kotlin.time.Duration

data class Spend(var id: String, var duration: Duration, var userID: String, var issueID: String, var date: LocalDate)
