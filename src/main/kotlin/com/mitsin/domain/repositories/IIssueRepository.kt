package com.mitsin.domain.repositories

import com.mitsin.domain.models.Issue

interface IIssueRepository {
    suspend fun getIssueByProjectAndUser(userID: String): List<Issue>
}