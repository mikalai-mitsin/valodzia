package com.valodzia.domain.repositories

import com.valodzia.domain.models.Issue

interface IIssueRepository {
    suspend fun getIssueByProjectAndUser(userID: String): List<Issue>
}