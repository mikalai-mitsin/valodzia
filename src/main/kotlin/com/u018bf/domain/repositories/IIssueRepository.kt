package com.u018bf.domain.repositories

import com.u018bf.domain.models.Issue
import kotlinx.datetime.LocalDate

interface IIssueRepository {
    fun save(issues: List<Issue>)
    suspend fun list(user: String?, from: LocalDate?, to: LocalDate?): List<Issue>
    fun clean()
}