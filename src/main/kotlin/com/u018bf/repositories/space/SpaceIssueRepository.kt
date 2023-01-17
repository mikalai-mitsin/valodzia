package com.u018bf.repositories.space

import com.u018bf.domain.models.Issue
import com.u018bf.domain.models.Spend
import com.u018bf.domain.repositories.IIssueRepository
import kotlinx.coroutines.*
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import space.jetbrains.api.runtime.BatchInfo
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.resources.projects
import space.jetbrains.api.runtime.resources.timeTracking
import space.jetbrains.api.runtime.types.IssueIdentifier
import space.jetbrains.api.runtime.types.IssuesSorting
import space.jetbrains.api.runtime.types.ProjectIdentifier
import space.jetbrains.api.runtime.types.TimeTrackingSubjectIdentifier

abstract class StringEntity(id: EntityID<String>) : Entity<String>(id)

abstract class StringEntityClass<out E : StringEntity> constructor(
    table: IdTable<String>,
    entityType: Class<E>? = null,
    entityCtor: ((EntityID<String>) -> E)? = null
) : EntityClass<String, E>(table, entityType, entityCtor)


class SpaceIssueRepository(private val space: SpaceClient) : IIssueRepository {

    override suspend fun list(user: String?, from: LocalDate?, to: LocalDate?): List<Issue> {
        val issues = mutableListOf<Issue>()
        val projects = space.projects.getAllProjects {
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
                        val spends = tracks.data.map { Spend("", it.duration, it.user.id, issue.id, it.date) }
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

    override fun save(issues: List<Issue>) {

    }

    override fun clean() {

    }

}
