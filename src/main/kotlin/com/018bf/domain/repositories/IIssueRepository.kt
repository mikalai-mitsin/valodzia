package com.`018bf`.domain.repositories

import com.`018bf`.domain.models.Issue

interface IIssueRepository {
    suspend fun getIssueByProjectAndUser(userID: String): List<Issue>
}