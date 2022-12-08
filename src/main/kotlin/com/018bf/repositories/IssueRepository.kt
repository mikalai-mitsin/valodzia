package com.`018bf`.repositories

import com.`018bf`.domain.models.Issue
import com.`018bf`.domain.models.Spend
import com.`018bf`.domain.repositories.IIssueRepository
import kotlinx.coroutines.*
import space.jetbrains.api.runtime.BatchInfo
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.resources.projects
import space.jetbrains.api.runtime.resources.timeTracking
import space.jetbrains.api.runtime.types.IssueIdentifier
import space.jetbrains.api.runtime.types.IssuesSorting
import space.jetbrains.api.runtime.types.ProjectIdentifier
import space.jetbrains.api.runtime.types.*
import space.jetbrains.api.runtime.types.TimeTrackingSubjectIdentifier

class IssueRepository(private val space: SpaceClient) : IIssueRepository {

    override suspend fun getIssueByProjectAndUser(userID: String): List<Issue> {
        val issues = mutableListOf<Issue>()
        val projects = space.projects.getAllProjectsByMember(
            member = ProfileIdentifier.Id(userID)
        ) {
            id()
            name()
        }
        projects.data.forEach { project ->
            val projectIssues = space.projects.planning.issues.getAllIssues(
                project = ProjectIdentifier.Id(project.id),
                sorting = IssuesSorting.CREATED,
                descending = false,
                batchInfo = BatchInfo(offset = null, batchSize = 1000)
            ) {
                id()
                title()
            }
            val t = CoroutineScope(Dispatchers.IO).launch {
                projectIssues.data.forEach { issue ->
                    delay(5L)
                    launch {
                        val tracks = space.timeTracking.items.getAllItems(
                            subject = TimeTrackingSubjectIdentifier.Issue(IssueIdentifier.Id(issue.id))
                        ) {
                            id()
                            duration()
                            date()
                            user {
                                id()
                            }
                        }
                        val spends = tracks.data.filter { it.user.id == userID }
                            .map { Spend(it.duration, it.user.id, issue.id, it.date) }
                        if (spends.isNotEmpty()) {
                            issues.add(
                                Issue(
                                    id = issue.id,
                                    title = issue.title,
                                    project = project.name,
                                    spends = spends
                                )
                            )
                        }
                    }

                }
            }
            t.join()
        }
        return issues
    }
}