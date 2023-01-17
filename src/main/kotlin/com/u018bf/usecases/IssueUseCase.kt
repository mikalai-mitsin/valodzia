package com.u018bf.usecases

import com.u018bf.domain.repositories.IIssueRepository
import com.u018bf.domain.repositories.ISpendRepository
import com.u018bf.domain.usecases.IIssueUseCase


class IssueUseCase(
    private val spaceIssueRepository: IIssueRepository,
    private val postgresIssueRepository: IIssueRepository,
    private val postgresSpendRepository: ISpendRepository,
) : IIssueUseCase {

    override suspend fun sync() {
        val issues = spaceIssueRepository.list(null, null, null)
        postgresIssueRepository.clean()
        postgresIssueRepository.save(issues)
        issues.map {
            postgresSpendRepository.save(it.spends)
        }
    }
}